package com.financetracker.auth.application;

import com.financetracker.auth.application.dto.AuthResult;
import com.financetracker.auth.application.dto.RegisterRequest;
import com.financetracker.auth.domain.PasswordHasher;
import com.financetracker.auth.domain.RefreshToken;
import com.financetracker.auth.domain.RefreshTokenRepository;
import com.financetracker.auth.domain.TokenService;
import com.financetracker.auth.domain.UserRepository;
import com.financetracker.shared.domain.TransactionPort;
import com.financetracker.shared.domain.User;
import com.financetracker.shared.domain.exception.ValidationException;
import lombok.RequiredArgsConstructor;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@RequiredArgsConstructor
public class RegisterUserUseCase {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final TokenService tokenService;
    private final PasswordHasher passwordHasher;
    private final TransactionPort transactionPort;

    public AuthResult execute(RegisterRequest request) {
        return transactionPort.execute(() -> {
            if (userRepository.existsByEmail(request.email())) {
                throw new ValidationException("Email already in use");
            }

            User user = User.create(
                    request.email(),
                    passwordHasher.hash(request.password()),
                    request.name(),
                    "USD"
            );
            user = userRepository.save(user);

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
