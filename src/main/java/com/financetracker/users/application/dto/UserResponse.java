package com.financetracker.users.application.dto;

import com.financetracker.shared.domain.User;

public record UserResponse(String id, String email, String name, String currency) {

    public static UserResponse from(User user) {
        return new UserResponse(
                user.getId().toString(),
                user.getEmail(),
                user.getName(),
                user.getCurrency()
        );
    }
}
