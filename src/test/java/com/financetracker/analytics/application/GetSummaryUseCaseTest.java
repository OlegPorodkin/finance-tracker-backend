package com.financetracker.analytics.application;

import com.financetracker.analytics.application.dto.SummaryResponse;
import com.financetracker.analytics.domain.AnalyticsRepository;
import com.financetracker.shared.domain.User;
import com.financetracker.shared.domain.UserId;
import com.financetracker.shared.domain.exception.NotFoundException;
import com.financetracker.transactions.domain.TransactionType;
import com.financetracker.users.domain.UserProfileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetSummaryUseCaseTest {

    @Mock AnalyticsRepository analyticsRepository;
    @Mock UserProfileRepository userProfileRepository;

    GetSummaryUseCase useCase;
    UserId userId = UserId.generate();

    @BeforeEach
    void setUp() {
        useCase = new GetSummaryUseCase(analyticsRepository, userProfileRepository);
    }

    @Test
    void summary_calculates_net_as_income_minus_expense() {
        User user = User.create("u@example.com", "hash", "User", "USD");
        when(userProfileRepository.findById(userId)).thenReturn(Optional.of(user));
        when(analyticsRepository.sumByType(userId, TransactionType.INCOME, null, null)).thenReturn(500_00L);
        when(analyticsRepository.sumByType(userId, TransactionType.EXPENSE, null, null)).thenReturn(200_00L);

        SummaryResponse result = useCase.execute(userId, null, null);

        assertThat(result.incomeInCents()).isEqualTo(500_00L);
        assertThat(result.expenseInCents()).isEqualTo(200_00L);
        assertThat(result.netInCents()).isEqualTo(300_00L);
        assertThat(result.currency()).isEqualTo("USD");
    }

    @Test
    void summary_net_is_negative_when_expenses_exceed_income() {
        User user = User.create("u@example.com", "hash", "User", "USD");
        when(userProfileRepository.findById(userId)).thenReturn(Optional.of(user));
        when(analyticsRepository.sumByType(userId, TransactionType.INCOME, null, null)).thenReturn(100_00L);
        when(analyticsRepository.sumByType(userId, TransactionType.EXPENSE, null, null)).thenReturn(300_00L);

        SummaryResponse result = useCase.execute(userId, null, null);

        assertThat(result.netInCents()).isEqualTo(-200_00L);
    }

    @Test
    void summary_passes_date_filter_to_repository() {
        User user = User.create("u@example.com", "hash", "User", "EUR");
        LocalDate from = LocalDate.of(2026, 1, 1);
        LocalDate to = LocalDate.of(2026, 1, 31);
        when(userProfileRepository.findById(userId)).thenReturn(Optional.of(user));
        when(analyticsRepository.sumByType(userId, TransactionType.INCOME, from, to)).thenReturn(400_00L);
        when(analyticsRepository.sumByType(userId, TransactionType.EXPENSE, from, to)).thenReturn(150_00L);

        SummaryResponse result = useCase.execute(userId, from, to);

        assertThat(result.netInCents()).isEqualTo(250_00L);
        assertThat(result.currency()).isEqualTo("EUR");
    }

    @Test
    void summary_throws_not_found_when_user_does_not_exist() {
        when(userProfileRepository.findById(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(userId, null, null))
                .isInstanceOf(NotFoundException.class);
    }
}
