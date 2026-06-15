package com.financetracker.auth.application;

import com.financetracker.auth.application.dto.AuthResult;
import com.financetracker.auth.application.dto.RegisterRequest;
import com.financetracker.auth.domain.PasswordHasher;
import com.financetracker.auth.domain.RefreshTokenRepository;
import com.financetracker.auth.domain.TokenService;
import com.financetracker.auth.domain.UserRepository;
import com.financetracker.shared.domain.TransactionPort;
import com.financetracker.shared.domain.User;
import com.financetracker.shared.domain.UserId;
import com.financetracker.shared.domain.exception.ConflictException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegisterUserUseCaseTest {

    @Mock UserRepository userRepository;
    @Mock RefreshTokenRepository refreshTokenRepository;
    @Mock TokenService tokenService;
    @Mock PasswordHasher passwordHasher;
    @Mock TransactionPort transactionPort;

    RegisterUserUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new RegisterUserUseCase(userRepository, refreshTokenRepository,
                tokenService, passwordHasher, transactionPort);
        when(transactionPort.execute(any(Supplier.class)))
                .thenAnswer(inv -> inv.<Supplier<?>>getArgument(0).get());
    }

    @Test
    void register_creates_user_and_returns_auth_result() {
        when(userRepository.existsByEmail("user@example.com")).thenReturn(false);
        when(passwordHasher.hash("password123")).thenReturn("hashed");
        User saved = User.create("user@example.com", "hashed", "User", "USD");
        when(userRepository.save(any())).thenReturn(saved);
        when(tokenService.generateAccessToken(any(), any())).thenReturn("access-token");
        when(tokenService.generateRefreshToken(any())).thenReturn("refresh-token");
        when(tokenService.hashToken("refresh-token")).thenReturn("refresh-hash");

        AuthResult result = useCase.execute(new RegisterRequest("user@example.com", "password123", "User"));

        assertThat(result.accessToken()).isEqualTo("access-token");
        assertThat(result.refreshToken()).isEqualTo("refresh-token");
        assertThat(result.user().getEmail()).isEqualTo("user@example.com");
        verify(refreshTokenRepository).save(any());
    }

    @Test
    void register_throws_conflict_when_email_already_exists() {
        when(userRepository.existsByEmail("user@example.com")).thenReturn(true);

        assertThatThrownBy(() ->
                useCase.execute(new RegisterRequest("user@example.com", "password123", "User")))
                .isInstanceOf(ConflictException.class)
                .hasMessage("Email already in use");

        verify(userRepository, never()).save(any());
    }
}
