package com.financetracker.budgets.application;

import com.financetracker.budgets.application.dto.BudgetResponse;
import com.financetracker.budgets.application.dto.UpdateBudgetRequest;
import com.financetracker.budgets.domain.Budget;
import com.financetracker.budgets.domain.BudgetPeriod;
import com.financetracker.budgets.domain.BudgetRepository;
import com.financetracker.shared.domain.TransactionPort;
import com.financetracker.shared.domain.UserId;
import com.financetracker.shared.domain.exception.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UpdateBudgetUseCaseTest {

    @Mock BudgetRepository budgetRepository;
    @Mock TransactionPort transactionPort;

    UpdateBudgetUseCase useCase;
    UserId userId = UserId.generate();

    @BeforeEach
    void setUp() {
        useCase = new UpdateBudgetUseCase(budgetRepository, transactionPort);
        when(transactionPort.execute(any(Supplier.class)))
                .thenAnswer(inv -> inv.<Supplier<?>>getArgument(0).get());
    }

    @Test
    void updates_budget_successfully() {
        Budget existing = Budget.create(userId, "cat-1", 100_00L, BudgetPeriod.MONTHLY, 70);
        when(budgetRepository.findById("budget-1", userId)).thenReturn(Optional.of(existing));
        when(budgetRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        BudgetResponse result = useCase.execute("budget-1", userId, new UpdateBudgetRequest(300_00L, 90));

        assertThat(result.limitAmountInCents()).isEqualTo(300_00L);
        assertThat(result.alertThreshold()).isEqualTo(90);
    }

    @Test
    void throws_not_found_when_budget_does_not_exist() {
        when(budgetRepository.findById("missing", userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute("missing", userId, new UpdateBudgetRequest(100_00L, 80)))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Budget not found");
    }
}
