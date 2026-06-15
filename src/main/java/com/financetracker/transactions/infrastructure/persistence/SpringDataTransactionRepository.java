package com.financetracker.transactions.infrastructure.persistence;

import com.financetracker.transactions.infrastructure.persistence.entity.TransactionJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SpringDataTransactionRepository
        extends JpaRepository<TransactionJpaEntity, UUID>,
                JpaSpecificationExecutor<TransactionJpaEntity> {

    Optional<TransactionJpaEntity> findByIdAndUserId(UUID id, UUID userId);

    @Query(value = """
            SELECT * FROM transactions
            WHERE user_id = :userId
              AND (CAST(:from AS DATE) IS NULL OR date >= CAST(:from AS DATE))
              AND (CAST(:to AS DATE) IS NULL OR date <= CAST(:to AS DATE))
            ORDER BY date DESC
            """, nativeQuery = true)
    List<TransactionJpaEntity> findAllForExport(@Param("userId") UUID userId,
                                                @Param("from") LocalDate from,
                                                @Param("to") LocalDate to);
}
