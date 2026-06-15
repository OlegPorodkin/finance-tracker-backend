package com.financetracker.analytics.application;

import com.financetracker.analytics.application.dto.MonthlyTrendResponse;
import com.financetracker.analytics.domain.AnalyticsRepository;
import com.financetracker.analytics.domain.MonthlyTotal;
import com.financetracker.shared.domain.UserId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetMonthlyTrendUseCaseTest {

    @Mock AnalyticsRepository analyticsRepository;

    GetMonthlyTrendUseCase useCase;
    UserId userId = UserId.generate();

    @BeforeEach
    void setUp() {
        useCase = new GetMonthlyTrendUseCase(analyticsRepository);
    }

    @Test
    void calculates_net_as_income_minus_expense() {
        when(analyticsRepository.sumByMonth(userId, 2026))
                .thenReturn(List.of(new MonthlyTotal(2026, 1, 500_00L, 200_00L)));

        List<MonthlyTrendResponse> result = useCase.execute(userId, 2026);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).netInCents()).isEqualTo(300_00L);
    }

    @Test
    void net_is_negative_when_expenses_exceed_income() {
        when(analyticsRepository.sumByMonth(userId, 2026))
                .thenReturn(List.of(new MonthlyTotal(2026, 3, 100_00L, 300_00L)));

        List<MonthlyTrendResponse> result = useCase.execute(userId, 2026);

        assertThat(result.get(0).netInCents()).isEqualTo(-200_00L);
    }

    @Test
    void maps_year_and_month_fields() {
        when(analyticsRepository.sumByMonth(userId, 2025))
                .thenReturn(List.of(new MonthlyTotal(2025, 6, 400_00L, 150_00L)));

        List<MonthlyTrendResponse> result = useCase.execute(userId, 2025);

        assertThat(result.get(0).year()).isEqualTo(2025);
        assertThat(result.get(0).month()).isEqualTo(6);
        assertThat(result.get(0).incomeInCents()).isEqualTo(400_00L);
        assertThat(result.get(0).expenseInCents()).isEqualTo(150_00L);
    }

    @Test
    void returns_empty_list_when_no_data() {
        when(analyticsRepository.sumByMonth(userId, 2020)).thenReturn(List.of());

        List<MonthlyTrendResponse> result = useCase.execute(userId, 2020);

        assertThat(result).isEmpty();
    }
}
