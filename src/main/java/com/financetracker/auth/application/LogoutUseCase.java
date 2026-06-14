package com.financetracker.auth.application;

import com.financetracker.auth.domain.RefreshTokenRepository;
import com.financetracker.auth.domain.TokenService;
import com.financetracker.shared.domain.TransactionPort;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class LogoutUseCase {

    private final RefreshTokenRepository refreshTokenRepository;
    private final TokenService tokenService;
    private final TransactionPort transactionPort;

    public void execute(String rawRefreshToken) {
        transactionPort.execute(() ->
                refreshTokenRepository.deleteByTokenHash(tokenService.hashToken(rawRefreshToken)));
    }
}
