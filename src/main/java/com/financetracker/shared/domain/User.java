package com.financetracker.shared.domain;

import lombok.Getter;

import java.time.Instant;

@Getter
public class User {

    private final UserId id;
    private String email;
    private String passwordHash;
    private String name;
    private String currency;
    private final Instant createdAt;
    private Instant updatedAt;

    public User(UserId id, String email, String passwordHash, String name, String currency,
                Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.email = email;
        this.passwordHash = passwordHash;
        this.name = name;
        this.currency = currency;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static User create(String email, String passwordHash, String name, String currency) {
        Instant now = Instant.now();
        return new User(UserId.generate(), email, passwordHash, name, currency, now, now);
    }

    public void updateProfile(String name, String currency) {
        this.name = name;
        this.currency = currency;
        this.updatedAt = Instant.now();
    }
}
