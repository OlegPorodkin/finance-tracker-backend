package com.financetracker.transactions.infrastructure.persistence.entity;

import com.financetracker.shared.domain.Money;
import com.financetracker.shared.domain.UserId;
import com.financetracker.transactions.domain.Transaction;
import com.financetracker.transactions.domain.TransactionType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "transactions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TransactionJpaEntity {

    @Id
    @Column(nullable = false, updatable = false)
    private UUID id;

    @Column(name = "user_id", nullable = false, updatable = false)
    private UUID userId;

    @Column(name = "category_id")
    private UUID categoryId; // nullable

    @Column(nullable = false, length = 20)
    private String type;

    @Column(name = "amount_in_cents", nullable = false)
    private long amountInCents;

    @Column(nullable = false, length = 10)
    private String currency;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false, length = 500)
    private String description;

    @Column(columnDefinition = "TEXT")
    private String notes; // nullable

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    public Transaction toTransaction() {
        return new Transaction(
                id.toString(),
                UserId.of(userId),
                TransactionType.valueOf(type),
                new Money(amountInCents, currency),
                date,
                description,
                categoryId != null ? categoryId.toString() : null,
                notes,
                createdAt,
                updatedAt
        );
    }

    public static TransactionJpaEntity fromTransaction(Transaction t) {
        UUID catId = t.getCategoryId() != null ? UUID.fromString(t.getCategoryId()) : null;
        return new TransactionJpaEntity(
                UUID.fromString(t.getId()),
                t.getUserId().value(),
                catId,
                t.getType().name(),
                t.getAmount().amountInCents(),
                t.getAmount().currency(),
                t.getDate(),
                t.getDescription(),
                t.getNotes(),
                t.getCreatedAt(),
                t.getUpdatedAt()
        );
    }
}
