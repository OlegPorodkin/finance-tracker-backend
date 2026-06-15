package com.financetracker.categories.application;

import com.financetracker.categories.application.dto.CategoryResponse;
import com.financetracker.categories.domain.CategoryRepository;
import com.financetracker.shared.domain.UserId;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class GetCategoriesUseCase {

    private final CategoryRepository categoryRepository;

    public List<CategoryResponse> execute(UserId userId) {
        return categoryRepository.findAllByUserId(userId)
                .stream()
                .map(CategoryResponse::from)
                .toList();
    }
}
