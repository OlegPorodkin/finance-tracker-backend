package com.financetracker.auth.infrastructure;

import com.financetracker.auth.application.LoginUseCase;
import com.financetracker.auth.application.LogoutUseCase;
import com.financetracker.auth.application.RefreshTokenUseCase;
import com.financetracker.auth.application.RegisterUserUseCase;
import com.financetracker.auth.domain.PasswordHasher;
import com.financetracker.auth.domain.RefreshTokenRepository;
import com.financetracker.auth.domain.TokenService;
import com.financetracker.auth.domain.UserRepository;
import com.financetracker.shared.domain.TransactionPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AuthUseCaseConfig {

    @Bean
    public RegisterUserUseCase registerUserUseCase(UserRepository userRepository,
                                                    RefreshTokenRepository refreshTokenRepository,
                                                    TokenService tokenService,
                                                    PasswordHasher passwordHasher,
                                                    TransactionPort transactionPort) {
        return new RegisterUserUseCase(userRepository, refreshTokenRepository, tokenService, passwordHasher, transactionPort);
    }

    @Bean
    public LoginUseCase loginUseCase(UserRepository userRepository,
                                      RefreshTokenRepository refreshTokenRepository,
                                      TokenService tokenService,
                                      PasswordHasher passwordHasher,
                                      TransactionPort transactionPort) {
        return new LoginUseCase(userRepository, refreshTokenRepository, tokenService, passwordHasher, transactionPort);
    }

    @Bean
    public RefreshTokenUseCase refreshTokenUseCase(RefreshTokenRepository refreshTokenRepository,
                                                    UserRepository userRepository,
                                                    TokenService tokenService,
                                                    TransactionPort transactionPort) {
        return new RefreshTokenUseCase(refreshTokenRepository, userRepository, tokenService, transactionPort);
    }

    @Bean
    public LogoutUseCase logoutUseCase(RefreshTokenRepository refreshTokenRepository,
                                        TokenService tokenService,
                                        TransactionPort transactionPort) {
        return new LogoutUseCase(refreshTokenRepository, tokenService, transactionPort);
    }
}
