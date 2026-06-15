package com.financetracker.analytics.domain;

public record MonthlyTotal(
        int year,
        int month,
        long incomeInCents,
        long expenseInCents
) {}
