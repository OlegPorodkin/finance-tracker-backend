package com.financetracker.auth.infrastructure.persistence;

import com.financetracker.auth.infrastructure.persistence.entity.RefreshTokenJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface SpringDataRefreshTokenRepository extends JpaRepository<RefreshTokenJpaEntity, UUID> {

    Optional<RefreshTokenJpaEntity> findByTokenHash(String tokenHash);

    @Modifying
    @Query("DELETE FROM RefreshTokenJpaEntity r WHERE r.tokenHash = :tokenHash")
    void deleteByTokenHash(@Param("tokenHash") String tokenHash);

    @Modifying
    @Query("DELETE FROM RefreshTokenJpaEntity r WHERE r.userId = :userId")
    void deleteAllByUserId(@Param("userId") UUID userId);
}
