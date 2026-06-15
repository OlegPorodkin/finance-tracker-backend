package com.financetracker.analytics.application.dto;

public record SummaryResponse(
        long incomeInCents,
        long expenseInCents,
        long netInCents,
        String currency
) {}
