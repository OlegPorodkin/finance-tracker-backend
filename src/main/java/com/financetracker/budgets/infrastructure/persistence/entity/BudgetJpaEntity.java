package com.financetracker.budgets.infrastructure.persistence.entity;

import com.financetracker.budgets.domain.Budget;
import com.financetracker.budgets.domain.BudgetPeriod;
import com.financetracker.shared.domain.UserId;
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
@Table(name = "budgets")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BudgetJpaEntity {

    @Id
    @Column(nullable = false, updatable = false)
    private UUID id;

    @Column(name = "user_id", nullable = false, updatable = false)
    private UUID userId;

    @Column(name = "category_id", nullable = false, updatable = false)
    private UUID categoryId;

    // field name differs from column name — Jackson serializes as "limitAmountInCents" ✓
    @Column(name = "limit_in_cents", nullable = false)
    private long limitAmountInCents;

    @Column(nullable = false, length = 20)
    private String period;

    @Column(name = "start_date", nullable = false, updatable = false)
    private LocalDate startDate;

    @Column(name = "alert_threshold", nullable = false)
    private int alertThreshold;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    public Budget toBudget() {
        return new Budget(
                id.toString(),
                UserId.of(userId),
                categoryId.toString(),
                limitAmountInCents,
                BudgetPeriod.valueOf(period),
                startDate,
                alertThreshold,
                createdAt,
                updatedAt
        );
    }

    public static BudgetJpaEntity fromBudget(Budget budget) {
        return new BudgetJpaEntity(
                UUID.fromString(budget.getId()),
                budget.getUserId().value(),
                UUID.fromString(budget.getCategoryId()),
                budget.getLimitAmountInCents(),
                budget.getPeriod().name(),
                budget.getStartDate(),
                budget.getAlertThreshold(),
                budget.getCreatedAt(),
                budget.getUpdatedAt()
        );
    }
}
