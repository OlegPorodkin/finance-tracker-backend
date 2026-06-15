package com.financetracker.budgets.application;

import com.financetracker.budgets.application.dto.BudgetStatusResponse;
import com.financetracker.budgets.domain.Budget;
import com.financetracker.budgets.domain.BudgetPeriod;
import com.financetracker.budgets.domain.BudgetRepository;
import com.financetracker.shared.domain.UserId;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
public class GetBudgetStatusUseCase {

    private final BudgetRepository budgetRepository;

    public List<BudgetStatusResponse> execute(UserId userId) {
        LocalDate today = LocalDate.now();
        return budgetRepository.findAllByUserId(userId)
                .stream()
                .map(budget -> toStatus(budget, userId, today))
                .toList();
    }

    private BudgetStatusResponse toStatus(Budget budget, UserId userId, LocalDate today) {
        long spent = budgetRepository.sumSpentInCents(userId, budget.getCategoryId(), budget.getStartDate(), today);
        long remaining = budget.getLimitAmountInCents() - spent;
        int percentage = budget.getLimitAmountInCents() > 0
                ? (int) Math.min((spent * 100) / budget.getLimitAmountInCents(), 100)
                : 0;
        return new BudgetStatusResponse(
                budget.getId(),
                budget.getCategoryId(),
                budget.getPeriod().name(),
                budget.getLimitAmountInCents(),
                spent,
                remaining,
                budget.getAlertThreshold(),
                percentage,
                percentage >= budget.getAlertThreshold(),
                budget.getStartDate(),
                endOfPeriod(budget.getPeriod(), budget.getStartDate())
        );
    }

    private LocalDate endOfPeriod(BudgetPeriod period, LocalDate startDate) {
        return switch (period) {
            case WEEKLY -> startDate.plusDays(6);
            case MONTHLY -> startDate.plusMonths(1).minusDays(1);
            case YEARLY -> startDate.plusYears(1).minusDays(1);
        };
    }
}
