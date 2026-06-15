package com.financetracker.categories.application;

import com.financetracker.categories.application.dto.CategoryResponse;
import com.financetracker.categories.application.dto.CreateCategoryRequest;
import com.financetracker.categories.domain.Category;
import com.financetracker.categories.domain.CategoryRepository;
import com.financetracker.categories.domain.CategoryType;
import com.financetracker.shared.domain.TransactionPort;
import com.financetracker.shared.domain.UserId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateCategoryUseCaseTest {

    @Mock CategoryRepository categoryRepository;
    @Mock TransactionPort transactionPort;

    CreateCategoryUseCase useCase;
    UserId userId = UserId.generate();

    @BeforeEach
    void setUp() {
        useCase = new CreateCategoryUseCase(categoryRepository, transactionPort);
        when(transactionPort.execute(any(Supplier.class)))
                .thenAnswer(inv -> inv.<Supplier<?>>getArgument(0).get());
    }

    @Test
    void creates_and_saves_category() {
        CreateCategoryRequest request = new CreateCategoryRequest("Food", CategoryType.EXPENSE, "#ff0", "food");
        when(categoryRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        CategoryResponse result = useCase.execute(userId, request);

        assertThat(result.name()).isEqualTo("Food");
        assertThat(result.type()).isEqualTo("EXPENSE");
        assertThat(result.isDefault()).isFalse();
        assertThat(result.isDeleted()).isFalse();
        verify(categoryRepository).save(any(Category.class));
    }
}
