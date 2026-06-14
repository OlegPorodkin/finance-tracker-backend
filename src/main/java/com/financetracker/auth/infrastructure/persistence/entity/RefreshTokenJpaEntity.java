package com.financetracker.auth.infrastructure.persistence.entity;

import com.financetracker.auth.domain.RefreshToken;
import com.financetracker.shared.domain.UserId;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "refresh_tokens")
@Getter
@Setter
@NoArgsConstructor
public class RefreshTokenJpaEntity {

    @Id
    private UUID id;

    @Column(name = "token_hash", nullable = false, unique = true)
    private String tokenHash;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    public RefreshToken toRefreshToken() {
        return new RefreshToken(id.toString(), tokenHash, UserId.of(userId), expiresAt, createdAt);
    }

    public static RefreshTokenJpaEntity fromRefreshToken(RefreshToken token) {
        RefreshTokenJpaEntity entity = new RefreshTokenJpaEntity();
        entity.setId(UUID.fromString(token.getId()));
        entity.setTokenHash(token.getTokenHash());
        entity.setUserId(token.getUserId().value());
        entity.setExpiresAt(token.getExpiresAt());
        entity.setCreatedAt(token.getCreatedAt());
        return entity;
    }
}
