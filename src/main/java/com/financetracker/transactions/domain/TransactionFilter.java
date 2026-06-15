package com.financetracker.transactions.domain;

import java.time.LocalDate;

public record TransactionFilter(
        LocalDate from,
        LocalDate to,
        TransactionType type,
        String categoryId,
        String search,
        int page,
        int size,
        String sort
) {}
