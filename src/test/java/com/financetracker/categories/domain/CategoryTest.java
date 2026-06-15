package com.financetracker.categories.domain;

import com.financetracker.shared.domain.UserId;
import com.financetracker.shared.domain.exception.ValidationException;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CategoryTest {

    @Test
    void create_sets_defaults() {
        UserId userId = UserId.generate();
        Category category = Category.create(userId, "Food", CategoryType.EXPENSE, "#ff0000", "food");

        assertThat(category.isDefault()).isFalse();
        assertThat(category.isDeleted()).isFalse();
        assertThat(category.getName()).isEqualTo("Food");
        assertThat(category.getType()).isEqualTo(CategoryType.EXPENSE);
        assertThat(category.getId()).isNotNull();
    }

    @Test
    void update_changes_fields() {
        Category category = Category.create(UserId.generate(), "Old", CategoryType.EXPENSE, "#000", "old");

        category.update("New", CategoryType.INCOME, "#fff", "new");

        assertThat(category.getName()).isEqualTo("New");
        assertThat(category.getType()).isEqualTo(CategoryType.INCOME);
        assertThat(category.getColor()).isEqualTo("#fff");
        assertThat(category.getIcon()).isEqualTo("new");
    }

    @Test
    void update_throws_when_category_is_default() {
        Category defaultCategory = new Category("id", null, "Salary", CategoryType.INCOME,
                "#000", "money", true, false, Instant.now(), Instant.now());

        assertThatThrownBy(() -> defaultCategory.update("Other", CategoryType.EXPENSE, "#fff", "other"))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("default");
    }

    @Test
    void soft_delete_marks_as_deleted() {
        Category category = Category.create(UserId.generate(), "Food", CategoryType.EXPENSE, "#ff0000", "food");

        category.softDelete();

        assertThat(category.isDeleted()).isTrue();
    }

    @Test
    void soft_delete_throws_when_category_is_default() {
        Category defaultCategory = new Category("id", null, "Salary", CategoryType.INCOME,
                "#000", "money", true, false, Instant.now(), Instant.now());

        assertThatThrownBy(defaultCategory::softDelete)
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("default");
    }
}
