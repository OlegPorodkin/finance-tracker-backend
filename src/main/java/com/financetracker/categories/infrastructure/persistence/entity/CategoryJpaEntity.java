package com.financetracker.categories.infrastructure.persistence.entity;

import com.financetracker.categories.domain.Category;
import com.financetracker.categories.domain.CategoryType;
import com.financetracker.shared.domain.UserId;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "categories")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CategoryJpaEntity {

    @Id
    @Column(nullable = false, updatable = false)
    private UUID id;

    @Column(name = "user_id")
    private UUID userId; // null for global defaults

    @Column(nullable = false, length = 255)
    private String name;

    @Column(nullable = false, length = 20)
    private String type;

    @Column(length = 20)
    private String color;

    @Column(length = 100)
    private String icon;

    @Column(name = "is_default", nullable = false)
    private boolean isDefault;

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    public Category toCategory() {
        return new Category(
                id.toString(),
                userId != null ? UserId.of(userId) : null,
                name,
                CategoryType.valueOf(type),
                color,
                icon,
                isDefault,
                isDeleted,
                createdAt,
                updatedAt
        );
    }

    public static CategoryJpaEntity fromCategory(Category category) {
        UUID uid = category.getUserId() != null ? category.getUserId().value() : null;
        return new CategoryJpaEntity(
                UUID.fromString(category.getId()),
                uid,
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
