package com.financetracker.budgets.application;

import com.financetracker.budgets.application.dto.BudgetResponse;
import com.financetracker.budgets.application.dto.CreateBudgetRequest;
import com.financetracker.budgets.domain.Budget;
import com.financetracker.budgets.domain.BudgetPeriod;
import com.financetracker.budgets.domain.BudgetRepository;
import com.financetracker.shared.domain.TransactionPort;
import com.financetracker.shared.domain.UserId;
import com.financetracker.shared.domain.exception.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateBudgetUseCaseTest {

    @Mock BudgetRepository budgetRepository;
    @Mock TransactionPort transactionPort;

    CreateBudgetUseCase useCase;
    UserId userId = UserId.generate();

    @BeforeEach
    void setUp() {
        useCase = new CreateBudgetUseCase(budgetRepository, transactionPort);
        when(transactionPort.execute(any(Supplier.class)))
                .thenAnswer(inv -> inv.<Supplier<?>>getArgument(0).get());
    }

    @Test
    void creates_budget_successfully() {
        CreateBudgetRequest request = new CreateBudgetRequest("cat-1", 200_00L, BudgetPeriod.MONTHLY, 80);
        when(budgetRepository.existsByUserIdAndCategoryIdAndPeriod(userId, "cat-1", BudgetPeriod.MONTHLY)).thenReturn(false);
        Budget saved = Budget.create(userId, "cat-1", 200_00L, BudgetPeriod.MONTHLY, 80);
        when(budgetRepository.save(any())).thenReturn(saved);

        BudgetResponse result = useCase.execute(userId, request);

        assertThat(result).isNotNull();
        assertThat(result.limitAmountInCents()).isEqualTo(200_00L);
        verify(budgetRepository).save(any());
    }

    @Test
    void throws_validation_exception_when_duplicate_budget() {
        CreateBudgetRequest request = new CreateBudgetRequest("cat-1", 200_00L, BudgetPeriod.MONTHLY, 80);
        when(budgetRepository.existsByUserIdAndCategoryIdAndPeriod(userId, "cat-1", BudgetPeriod.MONTHLY)).thenReturn(true);

        assertThatThrownBy(() -> useCase.execute(userId, request))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("already exists");

        verify(budgetRepository, never()).save(any());
    }
}
