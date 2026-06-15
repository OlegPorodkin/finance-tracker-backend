package com.financetracker.categories.domain;

import com.financetracker.shared.domain.UserId;
import com.financetracker.shared.domain.exception.ValidationException;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
public class Category {

    private final String id;
    private final UserId userId; // null for global defaults
    private String name;
    private CategoryType type;
    private String color;
    private String icon;
    private final boolean isDefault;
    private boolean isDeleted;
    private final Instant createdAt;
    private Instant updatedAt;

    public Category(String id, UserId userId, String name, CategoryType type,
                    String color, String icon, boolean isDefault, boolean isDeleted,
                    Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.type = type;
        this.color = color;
        this.icon = icon;
        this.isDefault = isDefault;
        this.isDeleted = isDeleted;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static Category create(UserId userId, String name, CategoryType type, String color, String icon) {
        Instant now = Instant.now();
        return new Category(UUID.randomUUID().toString(), userId, name, type, color, icon, false, false, now, now);
    }

    public void update(String name, CategoryType type, String color, String icon) {
        if (isDefault) throw new ValidationException("Cannot modify a default category");
        this.name = name;
        this.type = type;
        this.color = color;
        this.icon = icon;
        this.updatedAt = Instant.now();
    }

    public void softDelete() {
        if (isDefault) throw new ValidationException("Cannot delete a default category");
        this.isDeleted = true;
        this.updatedAt = Instant.now();
    }
}
