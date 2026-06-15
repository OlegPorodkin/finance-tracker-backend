package com.financetracker.budgets.application;

import com.financetracker.budgets.application.dto.BudgetResponse;
import com.financetracker.budgets.domain.BudgetRepository;
import com.financetracker.shared.domain.UserId;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class GetBudgetsUseCase {

    private final BudgetRepository budgetRepository;

    public List<BudgetResponse> execute(UserId userId) {
        return budgetRepository.findAllByUserId(userId)
                .stream()
                .map(BudgetResponse::from)
                .toList();
    }
}
