package com.financetracker.budgets.application.dto;

import com.financetracker.budgets.domain.Budget;

import java.time.Instant;
import java.time.LocalDate;

public record BudgetResponse(
        String id,
        String userId,
        String categoryId,
        long limitAmountInCents,
        String period,
        LocalDate startDate,
        int alertThreshold,
        Instant createdAt,
        Instant updatedAt
) {
    public static BudgetResponse from(Budget budget) {
        return new BudgetResponse(
                budget.getId(),
                budget.getUserId().value().toString(),
                budget.getCategoryId(),
                budget.getLimitAmountInCents(),
                budget.getPeriod().name(),
                budget.getStartDate(),
                budget.getAlertThreshold(),
                budget.getCreatedAt(),
                budget.getUpdatedAt()
        );
    }
}
