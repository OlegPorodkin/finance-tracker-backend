package com.financetracker.notifications.infrastructure;

import com.financetracker.budgets.domain.Budget;
import com.financetracker.budgets.domain.BudgetRepository;
import com.financetracker.categories.domain.CategoryRepository;
import com.financetracker.notifications.domain.NotificationPort;
import com.financetracker.users.domain.UserProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;

@Slf4j
@Component
@ConditionalOnProperty(name = "app.notifications.enabled", havingValue = "true")
@RequiredArgsConstructor
public class BudgetAlertJob {

    private final BudgetRepository budgetRepository;
    private final UserProfileRepository userProfileRepository;
    private final CategoryRepository categoryRepository;
    private final NotificationPort notificationPort;

    @Scheduled(cron = "${app.notifications.alert-job-cron}")
    public void checkBudgetAlerts() {
        log.debug("Running budget alert check");
        LocalDate today = LocalDate.now();
        budgetRepository.findAll().forEach(budget -> {
            try {
                processAlert(budget, today);
            } catch (Exception e) {
                log.error("Failed to process alert for budget {}", budget.getId(), e);
            }
        });
    }

    private void processAlert(Budget budget, LocalDate today) {
        Instant periodStart = budget.getStartDate().atStartOfDay(ZoneOffset.UTC).toInstant();
        if (budget.getLastAlertSentAt() != null
                && !budget.getLastAlertSentAt().isBefore(periodStart)) {
            return; // already sent for this period
        }

        long spent = budgetRepository.sumSpentInCents(
                budget.getUserId(), budget.getCategoryId(), budget.getStartDate(), today);
        int spentPercent = budget.getLimitAmountInCents() > 0
                ? (int) Math.min((spent * 100) / budget.getLimitAmountInCents(), 100)
                : 0;

        if (spentPercent < budget.getAlertThreshold()) {
            return;
        }

        userProfileRepository.findById(budget.getUserId()).ifPresent(user -> {
            String categoryName = categoryRepository
                    .findById(budget.getCategoryId(), budget.getUserId())
                    .map(c -> c.getName())
                    .orElse("Unknown");

            boolean sent = notificationPort.sendBudgetAlert(
                    user.getEmail(), categoryName, spentPercent,
                    budget.getLimitAmountInCents(), user.getCurrency());

            if (sent) {
                budget.markAlertSent();
                budgetRepository.save(budget);
            }
        });
    }
}
