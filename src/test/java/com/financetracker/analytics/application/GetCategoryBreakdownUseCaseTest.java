package com.financetracker.analytics.application;

import com.financetracker.analytics.application.dto.CategoryBreakdownResponse;
import com.financetracker.analytics.domain.AnalyticsRepository;
import com.financetracker.analytics.domain.CategoryAmount;
import com.financetracker.shared.domain.UserId;
import com.financetracker.transactions.domain.TransactionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetCategoryBreakdownUseCaseTest {

    @Mock AnalyticsRepository analyticsRepository;

    GetCategoryBreakdownUseCase useCase;
    UserId userId = UserId.generate();

    @BeforeEach
    void setUp() {
        useCase = new GetCategoryBreakdownUseCase(analyticsRepository);
    }

    @Test
    void calculates_percentage_for_each_category() {
        List<CategoryAmount> amounts = List.of(
                new CategoryAmount("cat-1", "Food", 300_00L),
                new CategoryAmount("cat-2", "Transport", 100_00L)
        );
        when(analyticsRepository.sumByCategoryAndType(userId, TransactionType.EXPENSE, null, null))
                .thenReturn(amounts);

        List<CategoryBreakdownResponse> result = useCase.execute(userId, TransactionType.EXPENSE, null, null);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).percentage()).isEqualTo(75);
        assertThat(result.get(1).percentage()).isEqualTo(25);
    }

    @Test
    void returns_zero_percentage_when_total_is_zero() {
        when(analyticsRepository.sumByCategoryAndType(userId, TransactionType.EXPENSE, null, null))
                .thenReturn(List.of());

        List<CategoryBreakdownResponse> result = useCase.execute(userId, TransactionType.EXPENSE, null, null);

        assertThat(result).isEmpty();
    }

    @Test
    void passes_type_and_date_filter_to_repository() {
        List<CategoryAmount> amounts = List.of(new CategoryAmount("cat-1", "Food", 100_00L));
        when(analyticsRepository.sumByCategoryAndType(userId, TransactionType.INCOME, null, null))
                .thenReturn(amounts);

        List<CategoryBreakdownResponse> result = useCase.execute(userId, TransactionType.INCOME, null, null);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).amountInCents()).isEqualTo(100_00L);
        assertThat(result.get(0).percentage()).isEqualTo(100);
    }

    @Test
    void rounds_percentage_to_nearest_integer() {
        List<CategoryAmount> amounts = List.of(
                new CategoryAmount("cat-1", "A", 1_00L),
                new CategoryAmount("cat-2", "B", 2_00L)
        );
        when(analyticsRepository.sumByCategoryAndType(userId, TransactionType.EXPENSE, null, null))
                .thenReturn(amounts);

        List<CategoryBreakdownResponse> result = useCase.execute(userId, TransactionType.EXPENSE, null, null);

        // 1/3 = 33%, 2/3 = 67%
        assertThat(result.get(0).percentage()).isEqualTo(33);
        assertThat(result.get(1).percentage()).isEqualTo(67);
    }
}
