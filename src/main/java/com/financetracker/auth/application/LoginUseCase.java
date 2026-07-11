package com.financetracker.auth.application;

import com.financetracker.auth.application.dto.AuthResult;
import com.financetracker.auth.application.dto.LoginRequest;
import com.financetracker.auth.domain.PasswordHasher;
import com.financetracker.auth.domain.UserRepository;
import com.financetracker.shared.domain.TransactionPort;
import com.financetracker.shared.domain.User;
import com.financetracker.shared.domain.exception.UnauthorizedException;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class LoginUseCase {

    private final UserRepository userRepository;
    private final PasswordHasher passwordHasher;
    private final TokenPairIssuer tokenPairIssuer;
    private final TransactionPort transactionPort;

    public AuthResult execute(LoginRequest request) {
        return transactionPort.execute(() -> {
            User user = userRepository.findByEmail(request.email())
                    .orElseThrow(() -> new UnauthorizedException("Invalid email or password"));

            if (!passwordHasher.matches(request.password(), user.getPasswordHash())) {
                throw new UnauthorizedException("Invalid email or password");
            }

            return tokenPairIssuer.issueFor(user);
        });
    }
}
