package com.financetracker.budgets.application.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public record UpdateBudgetRequest(
        @Min(1) long limitAmountInCents,
        @Min(0) @Max(100) int alertThreshold
) {}
