package com.financetracker.budgets.application.dto;

import com.financetracker.budgets.domain.BudgetPeriod;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateBudgetRequest(
        @NotBlank String categoryId,
        @Min(1) long limitAmountInCents,
        @NotNull BudgetPeriod period,
        @Min(0) @Max(100) int alertThreshold
) {}
