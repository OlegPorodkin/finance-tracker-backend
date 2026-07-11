package com.financetracker.auth.application;

import com.financetracker.auth.application.dto.AuthResult;
import com.financetracker.auth.domain.RefreshToken;
import com.financetracker.auth.domain.RefreshTokenRepository;
import com.financetracker.auth.domain.TokenService;
import com.financetracker.shared.domain.User;
import lombok.RequiredArgsConstructor;

import java.time.Duration;
import java.time.Instant;

@RequiredArgsConstructor
public class TokenPairIssuer {

    private static final Duration REFRESH_TOKEN_TTL = Duration.ofDays(7);

    private final TokenService tokenService;
    private final RefreshTokenRepository refreshTokenRepository;

    public AuthResult issueFor(User user) {
        String accessToken = tokenService.generateAccessToken(user.getId(), user.getEmail());
        String rawRefresh = tokenService.generateRefreshToken(user.getId());
        refreshTokenRepository.save(RefreshToken.create(
                tokenService.hashToken(rawRefresh),
                user.getId(),
                Instant.now().plus(REFRESH_TOKEN_TTL)
        ));
        return new AuthResult(user, accessToken, rawRefresh);
    }
}
