package com.financetracker.analytics.application.dto;

public record CategoryBreakdownResponse(
        String categoryId,
        String categoryName,
        long amountInCents,
        int percentage
) {}
