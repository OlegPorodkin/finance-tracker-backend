package com.financetracker.categories.application;

import com.financetracker.categories.application.dto.CategoryResponse;
import com.financetracker.categories.application.dto.UpdateCategoryRequest;
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
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UpdateCategoryUseCaseTest {

    @Mock CategoryRepository categoryRepository;
    @Mock TransactionPort transactionPort;

    UpdateCategoryUseCase useCase;
    UserId userId = UserId.generate();

    @BeforeEach
    void setUp() {
        useCase = new UpdateCategoryUseCase(categoryRepository, transactionPort);
        when(transactionPort.execute(any(Supplier.class)))
                .thenAnswer(inv -> inv.<Supplier<?>>getArgument(0).get());
    }

    @Test
    void updates_category_fields() {
        Category category = Category.create(userId, "Old", CategoryType.EXPENSE, "#000", "old");
        when(categoryRepository.findById("cat-1", userId)).thenReturn(Optional.of(category));
        when(categoryRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        CategoryResponse result = useCase.execute("cat-1", userId,
                new UpdateCategoryRequest("New", CategoryType.INCOME, "#fff", "new"));

        assertThat(result.name()).isEqualTo("New");
        assertThat(result.type()).isEqualTo("INCOME");
    }

    @Test
    void throws_not_found_when_category_does_not_exist() {
        when(categoryRepository.findById("missing", userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute("missing", userId,
                new UpdateCategoryRequest("Name", CategoryType.EXPENSE, null, null)))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Category not found");
    }
}
