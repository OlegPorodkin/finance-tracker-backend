package com.financetracker.auth.application;

import com.financetracker.auth.application.dto.AuthResult;
import com.financetracker.auth.application.dto.LoginRequest;
import com.financetracker.auth.domain.PasswordHasher;
import com.financetracker.auth.domain.RefreshTokenRepository;
import com.financetracker.auth.domain.TokenService;
import com.financetracker.auth.domain.UserRepository;
import com.financetracker.shared.domain.TransactionPort;
import com.financetracker.shared.domain.User;
import com.financetracker.shared.domain.exception.UnauthorizedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LoginUseCaseTest {

    @Mock UserRepository userRepository;
    @Mock RefreshTokenRepository refreshTokenRepository;
    @Mock TokenService tokenService;
    @Mock PasswordHasher passwordHasher;
    @Mock TransactionPort transactionPort;

    LoginUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new LoginUseCase(userRepository, refreshTokenRepository,
                tokenService, passwordHasher, transactionPort);
        when(transactionPort.execute(any(Supplier.class)))
                .thenAnswer(inv -> inv.<Supplier<?>>getArgument(0).get());
    }

    @Test
    void login_returns_tokens_for_valid_credentials() {
        User user = User.create("user@example.com", "hashed", "User", "USD");
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(passwordHasher.matches("password123", "hashed")).thenReturn(true);
        when(tokenService.generateAccessToken(any(), any())).thenReturn("access-token");
        when(tokenService.generateRefreshToken(any())).thenReturn("refresh-token");
        when(tokenService.hashToken("refresh-token")).thenReturn("refresh-hash");

        AuthResult result = useCase.execute(new LoginRequest("user@example.com", "password123"));

        assertThat(result.accessToken()).isEqualTo("access-token");
        assertThat(result.refreshToken()).isEqualTo("refresh-token");
    }

    @Test
    void login_throws_unauthorized_when_user_not_found() {
        when(userRepository.findByEmail("unknown@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(new LoginRequest("unknown@example.com", "pass")))
                .isInstanceOf(UnauthorizedException.class);
    }

    @Test
    void login_throws_unauthorized_when_password_is_wrong() {
        User user = User.create("user@example.com", "hashed", "User", "USD");
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(passwordHasher.matches("wrongpass", "hashed")).thenReturn(false);

        assertThatThrownBy(() -> useCase.execute(new LoginRequest("user@example.com", "wrongpass")))
                .isInstanceOf(UnauthorizedException.class);
    }
}
