package com.financetracker.auth.application;

import com.financetracker.auth.application.dto.AuthResult;
import com.financetracker.auth.domain.RefreshToken;
import com.financetracker.auth.domain.RefreshTokenRepository;
import com.financetracker.auth.domain.TokenService;
import com.financetracker.auth.domain.UserRepository;
import com.financetracker.shared.domain.TransactionPort;
import com.financetracker.shared.domain.User;
import com.financetracker.shared.domain.UserId;
import com.financetracker.shared.domain.exception.UnauthorizedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RefreshTokenUseCaseTest {

    @Mock RefreshTokenRepository refreshTokenRepository;
    @Mock UserRepository userRepository;
    @Mock TokenService tokenService;
    @Mock TransactionPort transactionPort;

    RefreshTokenUseCase useCase;

    private final UserId userId = UserId.generate();
    private final User user = User.create("user@example.com", "hash", "User", "USD");

    @BeforeEach
    void setUp() {
        useCase = new RefreshTokenUseCase(refreshTokenRepository, userRepository, tokenService, transactionPort);
        when(transactionPort.execute(any(Supplier.class)))
                .thenAnswer(inv -> inv.<Supplier<?>>getArgument(0).get());
    }

    @Test
    void refresh_rotates_token_and_returns_new_tokens() {
        RefreshToken stored = RefreshToken.create("stored-hash", userId, Instant.now().plus(7, ChronoUnit.DAYS));
        when(tokenService.hashToken("raw-token")).thenReturn("stored-hash");
        when(refreshTokenRepository.findByTokenHash("stored-hash")).thenReturn(Optional.of(stored));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(tokenService.generateAccessToken(any(), any())).thenReturn("new-access");
        when(tokenService.generateRefreshToken(any())).thenReturn("new-refresh");
        when(tokenService.hashToken("new-refresh")).thenReturn("new-hash");

        AuthResult result = useCase.execute("raw-token");

        assertThat(result.accessToken()).isEqualTo("new-access");
        assertThat(result.refreshToken()).isEqualTo("new-refresh");
        verify(refreshTokenRepository).deleteByTokenHash("stored-hash");
        verify(refreshTokenRepository).save(any());
    }

    @Test
    void refresh_throws_unauthorized_when_token_is_expired() {
        RefreshToken expired = RefreshToken.create("stored-hash", userId, Instant.now().minus(1, ChronoUnit.SECONDS));
        when(tokenService.hashToken("raw-token")).thenReturn("stored-hash");
        when(refreshTokenRepository.findByTokenHash("stored-hash")).thenReturn(Optional.of(expired));

        assertThatThrownBy(() -> useCase.execute("raw-token"))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessage("Refresh token expired");
    }

    @Test
    void refresh_detects_reuse_and_revokes_all_user_sessions() {
        when(tokenService.hashToken("raw-token")).thenReturn("unknown-hash");
        when(refreshTokenRepository.findByTokenHash("unknown-hash")).thenReturn(Optional.empty());
        when(tokenService.tryGetUserIdFromToken("raw-token")).thenReturn(Optional.of(userId));

        assertThatThrownBy(() -> useCase.execute("raw-token"))
                .isInstanceOf(UnauthorizedException.class);

        verify(refreshTokenRepository).deleteAllByUserId(userId);
    }
}
