package com.financetracker.transactions.application.dto;

import com.financetracker.transactions.domain.Transaction;

import java.time.Instant;
import java.time.LocalDate;

public record TransactionResponse(
        String id,
        String userId,
        String type,
        long amountInCents,
        String currency,
        LocalDate date,
        String description,
        String categoryId,
        String notes,
        Instant createdAt,
        Instant updatedAt
) {
    public static TransactionResponse from(Transaction transaction) {
        return new TransactionResponse(
                transaction.getId(),
                transaction.getUserId().value().toString(),
                transaction.getType().name(),
                transaction.getAmount().amountInCents(),
                transaction.getAmount().currency(),
                transaction.getDate(),
                transaction.getDescription(),
                transaction.getCategoryId(),
                transaction.getNotes(),
                transaction.getCreatedAt(),
                transaction.getUpdatedAt()
        );
    }
}
