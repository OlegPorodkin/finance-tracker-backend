package com.financetracker.auth.infrastructure;

import com.financetracker.auth.application.LoginUseCase;
import com.financetracker.auth.application.LogoutUseCase;
import com.financetracker.auth.application.RefreshTokenUseCase;
import com.financetracker.auth.application.RegisterUserUseCase;
import com.financetracker.auth.application.TokenPairIssuer;
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
    public TokenPairIssuer tokenPairIssuer(TokenService tokenService,
                                            RefreshTokenRepository refreshTokenRepository) {
        return new TokenPairIssuer(tokenService, refreshTokenRepository);
    }

    @Bean
    public RegisterUserUseCase registerUserUseCase(UserRepository userRepository,
                                                    PasswordHasher passwordHasher,
                                                    TokenPairIssuer tokenPairIssuer,
                                                    TransactionPort transactionPort) {
        return new RegisterUserUseCase(userRepository, passwordHasher, tokenPairIssuer, transactionPort);
    }

    @Bean
    public LoginUseCase loginUseCase(UserRepository userRepository,
                                      PasswordHasher passwordHasher,
                                      TokenPairIssuer tokenPairIssuer,
                                      TransactionPort transactionPort) {
        return new LoginUseCase(userRepository, passwordHasher, tokenPairIssuer, transactionPort);
    }

    @Bean
    public RefreshTokenUseCase refreshTokenUseCase(RefreshTokenRepository refreshTokenRepository,
                                                    UserRepository userRepository,
                                                    TokenService tokenService,
                                                    TokenPairIssuer tokenPairIssuer,
                                                    TransactionPort transactionPort) {
        return new RefreshTokenUseCase(refreshTokenRepository, userRepository, tokenService, tokenPairIssuer, transactionPort);
    }

    @Bean
    public LogoutUseCase logoutUseCase(RefreshTokenRepository refreshTokenRepository,
                                        TokenService tokenService,
                                        TransactionPort transactionPort) {
        return new LogoutUseCase(refreshTokenRepository, tokenService, transactionPort);
    }
}
