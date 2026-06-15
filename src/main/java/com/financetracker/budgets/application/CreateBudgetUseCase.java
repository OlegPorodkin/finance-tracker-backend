package com.financetracker.budgets.application;

import com.financetracker.budgets.application.dto.BudgetResponse;
import com.financetracker.budgets.application.dto.CreateBudgetRequest;
import com.financetracker.budgets.domain.Budget;
import com.financetracker.budgets.domain.BudgetRepository;
import com.financetracker.shared.domain.TransactionPort;
import com.financetracker.shared.domain.UserId;
import com.financetracker.shared.domain.exception.ValidationException;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CreateBudgetUseCase {

    private final BudgetRepository budgetRepository;
    private final TransactionPort transactionPort;

    public BudgetResponse execute(UserId userId, CreateBudgetRequest request) {
        return transactionPort.execute(() -> {
            if (budgetRepository.existsByUserIdAndCategoryIdAndPeriod(userId, request.categoryId(), request.period())) {
                throw new ValidationException("Budget already exists for this category and period");
            }
            Budget budget = Budget.create(userId, request.categoryId(), request.limitAmountInCents(),
                    request.period(), request.alertThreshold());
            return BudgetResponse.from(budgetRepository.save(budget));
        });
    }
}
