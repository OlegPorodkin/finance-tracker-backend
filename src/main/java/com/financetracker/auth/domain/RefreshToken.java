package com.financetracker.auth.domain;

import com.financetracker.shared.domain.UserId;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
public class RefreshToken {

    private final String id;
    private final String tokenHash;
    private final UserId userId;
    private final Instant expiresAt;
    private final Instant createdAt;

    public RefreshToken(String id, String tokenHash, UserId userId, Instant expiresAt, Instant createdAt) {
        this.id = id;
        this.tokenHash = tokenHash;
        this.userId = userId;
        this.expiresAt = expiresAt;
        this.createdAt = createdAt;
    }

    public static RefreshToken create(String tokenHash, UserId userId, Instant expiresAt) {
        return new RefreshToken(UUID.randomUUID().toString(), tokenHash, userId, expiresAt, Instant.now());
    }

    public boolean isExpired() {
        return Instant.now().isAfter(expiresAt);
    }
}
