package com.financetracker.categories.application.dto;

import com.financetracker.categories.domain.Category;
import com.financetracker.shared.domain.UserId;

import java.time.Instant;

public record CategoryResponse(
        String id,
        String userId,
        String name,
        String type,
        String color,
        String icon,
        Boolean isDefault,
        Boolean isDeleted,
        Instant createdAt,
        Instant updatedAt
) {
    public static CategoryResponse from(Category category) {
        UserId uid = category.getUserId();
        return new CategoryResponse(
                category.getId(),
                uid != null ? uid.value().toString() : null,
                category.getName(),
                category.getType().name(),
                category.getColor(),
                category.getIcon(),
                category.isDefault(),
                category.isDeleted(),
                category.getCreatedAt(),
                category.getUpdatedAt()
        );
    }
}
