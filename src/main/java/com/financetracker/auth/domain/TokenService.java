package com.financetracker.auth.domain;

import com.financetracker.shared.domain.UserId;

import java.util.Optional;

public interface TokenService {

    String generateAccessToken(UserId userId, String email);

    String generateRefreshToken(UserId userId);

    boolean validateToken(String token);

    UserId getUserIdFromToken(String token);

    /** Extracts userId without checking expiry — used for reuse detection. */
    Optional<UserId> tryGetUserIdFromToken(String token);

    String hashToken(String rawToken);
}
