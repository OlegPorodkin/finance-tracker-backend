package com.financetracker.budgets.application;

import com.financetracker.budgets.domain.BudgetRepository;
import com.financetracker.shared.domain.TransactionPort;
import com.financetracker.shared.domain.UserId;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DeleteBudgetUseCase {

    private final BudgetRepository budgetRepository;
    private final TransactionPort transactionPort;

    public void execute(String id, UserId userId) {
        transactionPort.execute(() -> budgetRepository.delete(id, userId));
    }
}
