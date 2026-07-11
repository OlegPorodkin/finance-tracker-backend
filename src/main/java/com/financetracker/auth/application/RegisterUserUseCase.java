package com.financetracker.auth.application;

import com.financetracker.auth.application.dto.AuthResult;
import com.financetracker.auth.application.dto.RegisterRequest;
import com.financetracker.auth.domain.PasswordHasher;
import com.financetracker.auth.domain.UserRepository;
import com.financetracker.shared.domain.TransactionPort;
import com.financetracker.shared.domain.User;
import com.financetracker.shared.domain.exception.ConflictException;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class RegisterUserUseCase {

    private final UserRepository userRepository;
    private final PasswordHasher passwordHasher;
    private final TokenPairIssuer tokenPairIssuer;
    private final TransactionPort transactionPort;

    public AuthResult execute(RegisterRequest request) {
        return transactionPort.execute(() -> {
            if (userRepository.existsByEmail(request.email())) {
                throw new ConflictException("Email already in use");
            }

            String currency = (request.currency() != null) ? request.currency() : "USD";
            User user = User.create(
                    request.email(),
                    passwordHasher.hash(request.password()),
                    request.name(),
                    currency
            );
            user = userRepository.save(user);

            return tokenPairIssuer.issueFor(user);
        });
    }
}
