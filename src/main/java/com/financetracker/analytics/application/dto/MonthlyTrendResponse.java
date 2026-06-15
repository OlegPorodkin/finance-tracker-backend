package com.financetracker.analytics.application.dto;

public record MonthlyTrendResponse(
        int year,
        int month,
        long incomeInCents,
        long expenseInCents,
        long netInCents
) {}
