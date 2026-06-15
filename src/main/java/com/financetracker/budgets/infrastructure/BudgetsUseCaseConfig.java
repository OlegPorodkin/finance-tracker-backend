package com.financetracker.budgets.infrastructure;

import com.financetracker.budgets.application.CreateBudgetUseCase;
import com.financetracker.budgets.application.DeleteBudgetUseCase;
import com.financetracker.budgets.application.GetBudgetStatusUseCase;
import com.financetracker.budgets.application.GetBudgetsUseCase;
import com.financetracker.budgets.application.UpdateBudgetUseCase;
import com.financetracker.budgets.domain.BudgetRepository;
import com.financetracker.shared.domain.TransactionPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BudgetsUseCaseConfig {

    @Bean
    public GetBudgetsUseCase getBudgetsUseCase(BudgetRepository budgetRepository) {
        return new GetBudgetsUseCase(budgetRepository);
    }

    @Bean
    public GetBudgetStatusUseCase getBudgetStatusUseCase(BudgetRepository budgetRepository) {
        return new GetBudgetStatusUseCase(budgetRepository);
    }

    @Bean
    public CreateBudgetUseCase createBudgetUseCase(BudgetRepository budgetRepository,
                                                    TransactionPort transactionPort) {
        return new CreateBudgetUseCase(budgetRepository, transactionPort);
    }

    @Bean
    public UpdateBudgetUseCase updateBudgetUseCase(BudgetRepository budgetRepository,
                                                    TransactionPort transactionPort) {
        return new UpdateBudgetUseCase(budgetRepository, transactionPort);
    }

    @Bean
    public DeleteBudgetUseCase deleteBudgetUseCase(BudgetRepository budgetRepository,
                                                    TransactionPort transactionPort) {
        return new DeleteBudgetUseCase(budgetRepository, transactionPort);
    }
}
