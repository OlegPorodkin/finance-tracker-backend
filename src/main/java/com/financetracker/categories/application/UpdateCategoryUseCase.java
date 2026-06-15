package com.financetracker.categories.application;

import com.financetracker.categories.application.dto.CategoryResponse;
import com.financetracker.categories.application.dto.UpdateCategoryRequest;
import com.financetracker.categories.domain.CategoryRepository;
import com.financetracker.shared.domain.TransactionPort;
import com.financetracker.shared.domain.UserId;
import com.financetracker.shared.domain.exception.NotFoundException;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UpdateCategoryUseCase {

    private final CategoryRepository categoryRepository;
    private final TransactionPort transactionPort;

    public CategoryResponse execute(String id, UserId userId, UpdateCategoryRequest request) {
        return transactionPort.execute(() -> {
            var category = categoryRepository.findById(id, userId)
                    .orElseThrow(() -> new NotFoundException("Category not found"));
            category.update(request.name(), request.type(), request.color(), request.icon());
            return CategoryResponse.from(categoryRepository.save(category));
        });
    }
}
