package com.financetracker.notifications.infrastructure;

import com.financetracker.budgets.domain.Budget;
import com.financetracker.budgets.domain.BudgetPeriod;
import com.financetracker.budgets.domain.BudgetRepository;
import com.financetracker.categories.domain.Category;
import com.financetracker.categories.domain.CategoryRepository;
import com.financetracker.categories.domain.CategoryType;
import com.financetracker.notifications.domain.NotificationPort;
import com.financetracker.shared.domain.User;
import com.financetracker.shared.domain.UserId;
import com.financetracker.users.domain.UserProfileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BudgetAlertJobTest {

    @Mock BudgetRepository budgetRepository;
    @Mock UserProfileRepository userProfileRepository;
    @Mock CategoryRepository categoryRepository;
    @Mock NotificationPort notificationPort;

    BudgetAlertJob job;
    UserId userId = UserId.generate();
    String categoryId = UUID.randomUUID().toString();
    User user = User.create("user@example.com", "hash", "Test User", "USD");

    @BeforeEach
    void setUp() {
        job = new BudgetAlertJob(budgetRepository, userProfileRepository, categoryRepository, notificationPort);
    }

    @Test
    void sends_alert_and_marks_sent_when_threshold_exceeded() {
        Budget budget = Budget.create(userId, categoryId, 100_00L, BudgetPeriod.MONTHLY, 80);
        when(budgetRepository.findAll()).thenReturn(List.of(budget));
        when(budgetRepository.sumSpentInCents(eq(userId), eq(categoryId), any(), any())).thenReturn(85_00L);
        when(userProfileRepository.findById(userId)).thenReturn(Optional.of(user));
        when(categoryRepository.findById(categoryId, userId)).thenReturn(Optional.of(category("Groceries")));
        when(notificationPort.sendBudgetAlert("user@example.com", "Groceries", 85, 100_00L, "USD")).thenReturn(true);

        job.checkBudgetAlerts();

        verify(notificationPort).sendBudgetAlert("user@example.com", "Groceries", 85, 100_00L, "USD");
        verify(budgetRepository).save(budget);
        assertThat(budget.getLastAlertSentAt()).isNotNull();
    }

    @Test
    void does_not_mark_sent_when_notification_delivery_fails() {
        Budget budget = Budget.create(userId, categoryId, 100_00L, BudgetPeriod.MONTHLY, 80);
        when(budgetRepository.findAll()).thenReturn(List.of(budget));
        when(budgetRepository.sumSpentInCents(eq(userId), eq(categoryId), any(), any())).thenReturn(85_00L);
        when(userProfileRepository.findById(userId)).thenReturn(Optional.of(user));
        when(categoryRepository.findById(categoryId, userId)).thenReturn(Optional.of(category("Groceries")));
        when(notificationPort.sendBudgetAlert("user@example.com", "Groceries", 85, 100_00L, "USD")).thenReturn(false);

        job.checkBudgetAlerts();

        verify(budgetRepository, never()).save(any());
        assertThat(budget.getLastAlertSentAt()).isNull();
    }

    @Test
    void does_not_send_alert_below_threshold() {
        Budget budget = Budget.create(userId, categoryId, 100_00L, BudgetPeriod.MONTHLY, 80);
        when(budgetRepository.findAll()).thenReturn(List.of(budget));
        when(budgetRepository.sumSpentInCents(eq(userId), eq(categoryId), any(), any())).thenReturn(50_00L);

        job.checkBudgetAlerts();

        verify(notificationPort, never()).sendBudgetAlert(anyString(), anyString(), anyInt(), anyLong(), anyString());
        verify(budgetRepository, never()).save(any());
    }

    @Test
    void skips_budget_when_alert_already_sent_this_period() {
        Budget budget = budgetWithLastAlert(Instant.now());
        when(budgetRepository.findAll()).thenReturn(List.of(budget));

        job.checkBudgetAlerts();

        verify(budgetRepository, never()).sumSpentInCents(any(), any(), any(), any());
        verify(notificationPort, never()).sendBudgetAlert(anyString(), anyString(), anyInt(), anyLong(), anyString());
    }

    @Test
    void sends_alert_when_last_alert_was_in_previous_period() {
        LocalDate startDate = LocalDate.now().withDayOfMonth(1);
        Instant previousPeriod = startDate.minusDays(5).atStartOfDay(ZoneOffset.UTC).toInstant();
        Budget budget = budgetWithLastAlert(previousPeriod);
        when(budgetRepository.findAll()).thenReturn(List.of(budget));
        when(budgetRepository.sumSpentInCents(eq(userId), eq(categoryId), any(), any())).thenReturn(90_00L);
        when(userProfileRepository.findById(userId)).thenReturn(Optional.of(user));
        when(categoryRepository.findById(categoryId, userId)).thenReturn(Optional.of(category("Groceries")));

        job.checkBudgetAlerts();

        verify(notificationPort).sendBudgetAlert("user@example.com", "Groceries", 90, 100_00L, "USD");
    }

    @Test
    void caps_spent_percentage_at_100_when_overspent() {
        Budget budget = Budget.create(userId, categoryId, 100_00L, BudgetPeriod.MONTHLY, 80);
        when(budgetRepository.findAll()).thenReturn(List.of(budget));
        when(budgetRepository.sumSpentInCents(eq(userId), eq(categoryId), any(), any())).thenReturn(250_00L);
        when(userProfileRepository.findById(userId)).thenReturn(Optional.of(user));
        when(categoryRepository.findById(categoryId, userId)).thenReturn(Optional.of(category("Groceries")));

        job.checkBudgetAlerts();

        verify(notificationPort).sendBudgetAlert("user@example.com", "Groceries", 100, 100_00L, "USD");
    }

    @Test
    void does_not_send_alert_when_user_not_found() {
        Budget budget = Budget.create(userId, categoryId, 100_00L, BudgetPeriod.MONTHLY, 80);
        when(budgetRepository.findAll()).thenReturn(List.of(budget));
        when(budgetRepository.sumSpentInCents(eq(userId), eq(categoryId), any(), any())).thenReturn(85_00L);
        when(userProfileRepository.findById(userId)).thenReturn(Optional.empty());

        job.checkBudgetAlerts();

        verify(notificationPort, never()).sendBudgetAlert(anyString(), anyString(), anyInt(), anyLong(), anyString());
        verify(budgetRepository, never()).save(any());
    }

    @Test
    void uses_unknown_category_name_when_category_missing() {
        Budget budget = Budget.create(userId, categoryId, 100_00L, BudgetPeriod.MONTHLY, 80);
        when(budgetRepository.findAll()).thenReturn(List.of(budget));
        when(budgetRepository.sumSpentInCents(eq(userId), eq(categoryId), any(), any())).thenReturn(85_00L);
        when(userProfileRepository.findById(userId)).thenReturn(Optional.of(user));
        when(categoryRepository.findById(categoryId, userId)).thenReturn(Optional.empty());

        job.checkBudgetAlerts();

        verify(notificationPort).sendBudgetAlert("user@example.com", "Unknown", 85, 100_00L, "USD");
    }

    @Test
    void continues_processing_remaining_budgets_when_one_fails() {
        Budget failing = Budget.create(userId, categoryId, 100_00L, BudgetPeriod.MONTHLY, 80);
        String otherCategoryId = UUID.randomUUID().toString();
        Budget healthy = Budget.create(userId, otherCategoryId, 100_00L, BudgetPeriod.MONTHLY, 80);
        when(budgetRepository.findAll()).thenReturn(List.of(failing, healthy));
        doThrow(new RuntimeException("db error"))
                .when(budgetRepository).sumSpentInCents(eq(userId), eq(categoryId), any(), any());
        when(budgetRepository.sumSpentInCents(eq(userId), eq(otherCategoryId), any(), any())).thenReturn(85_00L);
        when(userProfileRepository.findById(userId)).thenReturn(Optional.of(user));
        when(categoryRepository.findById(otherCategoryId, userId)).thenReturn(Optional.of(category("Transport")));

        job.checkBudgetAlerts();

        verify(notificationPort).sendBudgetAlert("user@example.com", "Transport", 85, 100_00L, "USD");
    }

    @Test
    void does_not_send_alert_when_limit_is_zero() {
        Budget budget = new Budget(UUID.randomUUID().toString(), userId, categoryId, 0L,
                BudgetPeriod.MONTHLY, LocalDate.now().withDayOfMonth(1), 80,
                Instant.now(), Instant.now(), null);
        when(budgetRepository.findAll()).thenReturn(List.of(budget));
        when(budgetRepository.sumSpentInCents(eq(userId), eq(categoryId), any(), any())).thenReturn(50_00L);

        job.checkBudgetAlerts();

        verify(notificationPort, never()).sendBudgetAlert(anyString(), anyString(), anyInt(), anyLong(), anyString());
    }

    private Budget budgetWithLastAlert(Instant lastAlertSentAt) {
        Instant now = Instant.now();
        return new Budget(UUID.randomUUID().toString(), userId, categoryId, 100_00L,
                BudgetPeriod.MONTHLY, LocalDate.now().withDayOfMonth(1), 80,
                now, now, lastAlertSentAt);
    }

    private Category category(String name) {
        return Category.create(userId, name, CategoryType.EXPENSE, "#ff0000", "cart");
    }
}
