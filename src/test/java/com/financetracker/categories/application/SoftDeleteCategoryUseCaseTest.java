package com.financetracker.categories.application;

import com.financetracker.categories.domain.Category;
import com.financetracker.categories.domain.CategoryRepository;
import com.financetracker.categories.domain.CategoryType;
import com.financetracker.shared.domain.TransactionPort;
import com.financetracker.shared.domain.UserId;
import com.financetracker.shared.domain.exception.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SoftDeleteCategoryUseCaseTest {

    @Mock CategoryRepository categoryRepository;
    @Mock TransactionPort transactionPort;

    SoftDeleteCategoryUseCase useCase;
    UserId userId = UserId.generate();

    @BeforeEach
    void setUp() {
        useCase = new SoftDeleteCategoryUseCase(categoryRepository, transactionPort);
        doAnswer(inv -> { inv.<Runnable>getArgument(0).run(); return null; })
                .when(transactionPort).execute(any(Runnable.class));
    }

    @Test
    void soft_deletes_category() {
        Category category = Category.create(userId, "Food", CategoryType.EXPENSE, "#000", "food");
        when(categoryRepository.findById("cat-1", userId)).thenReturn(Optional.of(category));

        useCase.execute("cat-1", userId);

        verify(categoryRepository).save(category);
    }

    @Test
    void throws_not_found_when_category_does_not_exist() {
        when(categoryRepository.findById("missing", userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute("missing", userId))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Category not found");
    }
}
