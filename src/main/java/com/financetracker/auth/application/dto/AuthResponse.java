package com.financetracker.auth.application.dto;

import com.financetracker.shared.domain.User;

public record AuthResponse(UserInfo user) {

    public record UserInfo(String id, String email, String name, String currency) {}

    public static AuthResponse from(User user) {
        return new AuthResponse(new UserInfo(
                user.getId().toString(),
                user.getEmail(),
                user.getName(),
                user.getCurrency()
        ));
    }
}
