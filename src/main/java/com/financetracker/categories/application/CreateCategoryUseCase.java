package com.financetracker.categories.application;

import com.financetracker.categories.application.dto.CategoryResponse;
import com.financetracker.categories.application.dto.CreateCategoryRequest;
import com.financetracker.categories.domain.Category;
import com.financetracker.categories.domain.CategoryRepository;
import com.financetracker.shared.domain.TransactionPort;
import com.financetracker.shared.domain.UserId;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CreateCategoryUseCase {

    private final CategoryRepository categoryRepository;
    private final TransactionPort transactionPort;

    public CategoryResponse execute(UserId userId, CreateCategoryRequest request) {
        return transactionPort.execute(() -> {
            Category category = Category.create(userId, request.name(), request.type(), request.color(), request.icon());
            return CategoryResponse.from(categoryRepository.save(category));
        });
    }
}
