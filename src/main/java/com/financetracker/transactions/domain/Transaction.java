package com.financetracker.transactions.domain;

import com.financetracker.shared.domain.Money;
import com.financetracker.shared.domain.UserId;
import lombok.Getter;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

@Getter
public class Transaction {

    private final String id;
    private final UserId userId;
    private final TransactionType type;
    private Money amount;
    private LocalDate date;
    private String description;
    private String categoryId; // nullable
    private String notes;      // nullable
    private final Instant createdAt;
    private Instant updatedAt;

    public Transaction(String id, UserId userId, TransactionType type, Money amount,
                       LocalDate date, String description, String categoryId, String notes,
                       Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.userId = userId;
        this.type = type;
        this.amount = amount;
        this.date = date;
        this.description = description;
        this.categoryId = categoryId;
        this.notes = notes;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static Transaction create(UserId userId, TransactionType type, Money amount,
                                     LocalDate date, String description, String categoryId, String notes) {
        Instant now = Instant.now();
        return new Transaction(UUID.randomUUID().toString(), userId, type, amount,
                date, description, categoryId, notes, now, now);
    }

    // type is intentionally not updatable — financial audit rule
    public void update(Money amount, LocalDate date, String description, String categoryId, String notes) {
        this.amount = Objects.requireNonNull(amount);
        this.date = Objects.requireNonNull(date);
        this.description = Objects.requireNonNull(description);
        this.categoryId = categoryId;
        this.notes = notes;
        this.updatedAt = Instant.now();
    }
}
