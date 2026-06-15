package com.financetracker.budgets.application;

import com.financetracker.budgets.application.dto.BudgetResponse;
import com.financetracker.budgets.application.dto.UpdateBudgetRequest;
import com.financetracker.budgets.domain.BudgetRepository;
import com.financetracker.shared.domain.TransactionPort;
import com.financetracker.shared.domain.UserId;
import com.financetracker.shared.domain.exception.NotFoundException;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UpdateBudgetUseCase {

    private final BudgetRepository budgetRepository;
    private final TransactionPort transactionPort;

    public BudgetResponse execute(String id, UserId userId, UpdateBudgetRequest request) {
        return transactionPort.execute(() -> {
            var budget = budgetRepository.findById(id, userId)
                    .orElseThrow(() -> new NotFoundException("Budget not found"));
            budget.update(request.limitAmountInCents(), request.alertThreshold());
            return BudgetResponse.from(budgetRepository.save(budget));
        });
    }
}
