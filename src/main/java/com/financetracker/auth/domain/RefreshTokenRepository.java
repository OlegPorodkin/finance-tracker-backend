package com.financetracker.auth.domain;

import com.financetracker.shared.domain.UserId;

import java.util.Optional;

public interface RefreshTokenRepository {

    Optional<RefreshToken> findByTokenHash(String tokenHash);

    RefreshToken save(RefreshToken token);

    void deleteByTokenHash(String tokenHash);

    void deleteAllByUserId(UserId userId);
}
