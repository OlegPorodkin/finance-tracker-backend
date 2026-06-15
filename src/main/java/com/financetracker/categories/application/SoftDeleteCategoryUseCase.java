package com.financetracker.categories.application;

import com.financetracker.categories.domain.CategoryRepository;
import com.financetracker.shared.domain.TransactionPort;
import com.financetracker.shared.domain.UserId;
import com.financetracker.shared.domain.exception.NotFoundException;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SoftDeleteCategoryUseCase {

    private final CategoryRepository categoryRepository;
    private final TransactionPort transactionPort;

    public void execute(String id, UserId userId) {
        transactionPort.execute(() -> {
            var category = categoryRepository.findById(id, userId)
                    .orElseThrow(() -> new NotFoundException("Category not found"));
            category.softDelete();
            categoryRepository.save(category);
        });
    }
}
