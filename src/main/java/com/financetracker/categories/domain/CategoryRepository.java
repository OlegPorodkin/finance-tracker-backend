package com.financetracker.categories.domain;

import com.financetracker.shared.domain.UserId;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository {

    // Returns user's own categories + global defaults (is_default=true, user_id=NULL), including is_deleted=true
    List<Category> findAllByUserId(UserId userId);

    // Finds a category that belongs to the user OR is a global default
    Optional<Category> findById(String id, UserId userId);

    Category save(Category category);
}
