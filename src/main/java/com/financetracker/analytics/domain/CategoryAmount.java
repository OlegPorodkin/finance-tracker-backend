package com.financetracker.analytics.domain;

public record CategoryAmount(
        String categoryId,
        String categoryName,
        long amountInCents
) {}
