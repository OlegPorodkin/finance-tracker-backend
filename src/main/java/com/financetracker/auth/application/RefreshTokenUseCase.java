package com.financetracker.auth.application;

import com.financetracker.auth.application.dto.AuthResult;
import com.financetracker.auth.domain.RefreshToken;
import com.financetracker.auth.domain.RefreshTokenRepository;
import com.financetracker.auth.domain.TokenService;
import com.financetracker.auth.domain.UserRepository;
import com.financetracker.shared.domain.TransactionPort;
import com.financetracker.shared.domain.User;
import com.financetracker.shared.domain.exception.NotFoundException;
import com.financetracker.shared.domain.exception.UnauthorizedException;
import lombok.RequiredArgsConstructor;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@RequiredArgsConstructor
public class RefreshTokenUseCase {

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;
    private final TokenService tokenService;
    private final TransactionPort transactionPort;

    public AuthResult execute(String rawToken) {
        return transactionPort.execute(() -> {
            String tokenHash = tokenService.hashToken(rawToken);
            RefreshToken stored = refreshTokenRepository.findByTokenHash(tokenHash)
                    .orElseGet(() -> handleReuse(rawToken));

            if (stored.isExpired()) {
                refreshTokenRepository.deleteByTokenHash(tokenHash);
                throw new UnauthorizedException("Refresh token expired");
            }

            refreshTokenRepository.deleteByTokenHash(tokenHash);

            User user = userRepository.findById(stored.getUserId())
                    .orElseThrow(() -> new NotFoundException("User not found"));

            String newAccessToken = tokenService.generateAccessToken(user.getId(), user.getEmail());
            String newRawRefresh = tokenService.generateRefreshToken(user.getId());
            refreshTokenRepository.save(RefreshToken.create(
                    tokenService.hashToken(newRawRefresh),
                    user.getId(),
                    Instant.now().plus(7, ChronoUnit.DAYS)
            ));

            return new AuthResult(user, newAccessToken, newRawRefresh);
        });
    }

    private RefreshToken handleReuse(String rawToken) {
        tokenService.tryGetUserIdFromToken(rawToken).ifPresent(
                refreshTokenRepository::deleteAllByUserId);
        throw new UnauthorizedException("Invalid refresh token");
    }
}
