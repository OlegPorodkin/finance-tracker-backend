package com.financetracker.auth.application.dto;

import com.financetracker.shared.domain.User;

public record AuthResult(User user, String accessToken, String refreshToken) {}
