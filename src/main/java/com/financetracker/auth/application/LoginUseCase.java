package com.financetracker.auth.application;

import com.financetracker.auth.application.dto.AuthResult;
import com.financetracker.auth.application.dto.LoginRequest;
import com.financetracker.auth.domain.PasswordHasher;
import com.financetracker.auth.domain.RefreshToken;
import com.financetracker.auth.domain.RefreshTokenRepository;
import com.financetracker.auth.domain.TokenService;
import com.financetracker.auth.domain.UserRepository;
import com.financetracker.shared.domain.TransactionPort;
import com.financetracker.shared.domain.User;
import com.financetracker.shared.domain.exception.UnauthorizedException;
import lombok.RequiredArgsConstructor;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@RequiredArgsConstructor
public class LoginUseCase {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final TokenService tokenService;
    private final PasswordHasher passwordHasher;
    private final TransactionPort transactionPort;

    public AuthResult execute(LoginRequest request) {
        return transactionPort.execute(() -> {
            User user = userRepository.findByEmail(request.email())
                    .orElseThrow(() -> new UnauthorizedException("Invalid email or password"));

            if (!passwordHasher.matches(request.password(), user.getPasswordHash())) {
                throw new UnauthorizedException("Invalid email or password");
            }

            String accessToken = tokenService.generateAccessToken(user.getId(), user.getEmail());
            String rawRefresh = tokenService.generateRefreshToken(user.getId());
            refreshTokenRepository.save(RefreshToken.create(
                    tokenService.hashToken(rawRefresh),
                    user.getId(),
                    Instant.now().plus(7, ChronoUnit.DAYS)
            ));

            return new AuthResult(user, accessToken, rawRefresh);
        });
    }
}
