package com.financetracker.categories.infrastructure;

import com.financetracker.categories.application.CreateCategoryUseCase;
import com.financetracker.categories.application.GetCategoriesUseCase;
import com.financetracker.categories.application.SoftDeleteCategoryUseCase;
import com.financetracker.categories.application.UpdateCategoryUseCase;
import com.financetracker.categories.domain.CategoryRepository;
import com.financetracker.shared.domain.TransactionPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CategoriesUseCaseConfig {

    @Bean
    public GetCategoriesUseCase getCategoriesUseCase(CategoryRepository categoryRepository) {
        return new GetCategoriesUseCase(categoryRepository);
    }

    @Bean
    public CreateCategoryUseCase createCategoryUseCase(CategoryRepository categoryRepository,
                                                        TransactionPort transactionPort) {
        return new CreateCategoryUseCase(categoryRepository, transactionPort);
    }

    @Bean
    public UpdateCategoryUseCase updateCategoryUseCase(CategoryRepository categoryRepository,
                                                        TransactionPort transactionPort) {
        return new UpdateCategoryUseCase(categoryRepository, transactionPort);
    }

    @Bean
    public SoftDeleteCategoryUseCase softDeleteCategoryUseCase(CategoryRepository categoryRepository,
                                                                TransactionPort transactionPort) {
        return new SoftDeleteCategoryUseCase(categoryRepository, transactionPort);
    }
}
