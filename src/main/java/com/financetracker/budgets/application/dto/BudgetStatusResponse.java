package com.financetracker.budgets.application.dto;

import java.time.LocalDate;

public record BudgetStatusResponse(
        String id,
        String categoryId,
        String period,
        long limitAmountInCents,
        long spentInCents,
        long remainingInCents,
        int alertThreshold,
        int spentPercentage,
        boolean alertTriggered,
        LocalDate startDate,
        LocalDate endDate
) {}
