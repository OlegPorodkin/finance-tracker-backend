package com.financetracker.budgets.infrastructure.persistence;

import com.financetracker.budgets.infrastructure.persistence.entity.BudgetJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SpringDataBudgetRepository extends JpaRepository<BudgetJpaEntity, UUID> {

    List<BudgetJpaEntity> findAllByUserId(UUID userId);

    Optional<BudgetJpaEntity> findByIdAndUserId(UUID id, UUID userId);

    boolean existsByUserIdAndCategoryIdAndPeriod(UUID userId, UUID categoryId, String period);

    // Native query crosses into transactions table — avoids cross-module JPA entity dependency
    @Query(value = """
            SELECT COALESCE(SUM(amount_in_cents), 0)
            FROM transactions
            WHERE user_id = :userId
              AND category_id = :categoryId
              AND date >= :from
              AND date <= :to
              AND type = 'EXPENSE'
            """, nativeQuery = true)
    long sumSpentInCents(@Param("userId") UUID userId,
                         @Param("categoryId") UUID categoryId,
                         @Param("from") LocalDate from,
                         @Param("to") LocalDate to);
}
