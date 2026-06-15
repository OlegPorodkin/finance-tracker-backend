package com.financetracker.auth.application;

import com.financetracker.auth.domain.RefreshTokenRepository;
import com.financetracker.auth.domain.TokenService;
import com.financetracker.shared.domain.TransactionPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LogoutUseCaseTest {

    @Mock RefreshTokenRepository refreshTokenRepository;
    @Mock TokenService tokenService;
    @Mock TransactionPort transactionPort;

    LogoutUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new LogoutUseCase(refreshTokenRepository, tokenService, transactionPort);
        doAnswer(inv -> { inv.<Runnable>getArgument(0).run(); return null; })
                .when(transactionPort).execute(any(Runnable.class));
    }

    @Test
    void logout_deletes_token_by_hash() {
        when(tokenService.hashToken("raw-token")).thenReturn("hashed");

        useCase.execute("raw-token");

        verify(refreshTokenRepository).deleteByTokenHash("hashed");
    }
}
