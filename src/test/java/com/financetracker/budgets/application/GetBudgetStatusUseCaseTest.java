package com.financetracker.budgets.application;

import com.financetracker.budgets.application.dto.BudgetStatusResponse;
import com.financetracker.budgets.domain.Budget;
import com.financetracker.budgets.domain.BudgetPeriod;
import com.financetracker.budgets.domain.BudgetRepository;
import com.financetracker.shared.domain.UserId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetBudgetStatusUseCaseTest {

    @Mock BudgetRepository budgetRepository;

    GetBudgetStatusUseCase useCase;
    UserId userId = UserId.generate();

    @BeforeEach
    void setUp() {
        useCase = new GetBudgetStatusUseCase(budgetRepository);
    }

    @Test
    void status_calculates_spent_percentage_correctly() {
        Budget budget = Budget.create(userId, "cat-1", 100_00L, BudgetPeriod.MONTHLY, 80);
        when(budgetRepository.findAllByUserId(userId)).thenReturn(List.of(budget));
        when(budgetRepository.sumSpentInCents(eq(userId), eq("cat-1"), any(), any())).thenReturn(60_00L);

        List<BudgetStatusResponse> result = useCase.execute(userId);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).spentInCents()).isEqualTo(60_00L);
        assertThat(result.get(0).remainingInCents()).isEqualTo(40_00L);
        assertThat(result.get(0).spentPercentage()).isEqualTo(60);
    }

    @Test
    void status_caps_percentage_at_100_when_overspent() {
        Budget budget = Budget.create(userId, "cat-1", 100_00L, BudgetPeriod.MONTHLY, 80);
        when(budgetRepository.findAllByUserId(userId)).thenReturn(List.of(budget));
        when(budgetRepository.sumSpentInCents(eq(userId), eq("cat-1"), any(), any())).thenReturn(150_00L);

        List<BudgetStatusResponse> result = useCase.execute(userId);

        assertThat(result.get(0).spentPercentage()).isEqualTo(100);
    }

    @Test
    void status_triggers_alert_when_threshold_exceeded() {
        Budget budget = Budget.create(userId, "cat-1", 100_00L, BudgetPeriod.MONTHLY, 80);
        when(budgetRepository.findAllByUserId(userId)).thenReturn(List.of(budget));
        when(budgetRepository.sumSpentInCents(eq(userId), eq("cat-1"), any(), any())).thenReturn(85_00L);

        List<BudgetStatusResponse> result = useCase.execute(userId);

        assertThat(result.get(0).alertTriggered()).isTrue();
    }

    @Test
    void status_does_not_trigger_alert_below_threshold() {
        Budget budget = Budget.create(userId, "cat-1", 100_00L, BudgetPeriod.MONTHLY, 80);
        when(budgetRepository.findAllByUserId(userId)).thenReturn(List.of(budget));
        when(budgetRepository.sumSpentInCents(eq(userId), eq("cat-1"), any(), any())).thenReturn(50_00L);

        List<BudgetStatusResponse> result = useCase.execute(userId);

        assertThat(result.get(0).alertTriggered()).isFalse();
    }

    @Test
    void status_calculates_end_date_for_weekly_period() {
        Budget budget = Budget.create(userId, null, 100_00L, BudgetPeriod.WEEKLY, 80);
        when(budgetRepository.findAllByUserId(userId)).thenReturn(List.of(budget));
        when(budgetRepository.sumSpentInCents(any(), any(), any(), any())).thenReturn(0L);

        List<BudgetStatusResponse> result = useCase.execute(userId);

        LocalDate start = result.get(0).startDate();
        assertThat(result.get(0).endDate()).isEqualTo(start.plusDays(6));
    }

    @Test
    void status_calculates_end_date_for_monthly_period() {
        Budget budget = Budget.create(userId, null, 100_00L, BudgetPeriod.MONTHLY, 80);
        when(budgetRepository.findAllByUserId(userId)).thenReturn(List.of(budget));
        when(budgetRepository.sumSpentInCents(any(), any(), any(), any())).thenReturn(0L);

        List<BudgetStatusResponse> result = useCase.execute(userId);

        LocalDate start = result.get(0).startDate();
        assertThat(result.get(0).endDate()).isEqualTo(start.plusMonths(1).minusDays(1));
    }

    @Test
    void status_calculates_end_date_for_yearly_period() {
        Budget budget = Budget.create(userId, null, 100_00L, BudgetPeriod.YEARLY, 80);
        when(budgetRepository.findAllByUserId(userId)).thenReturn(List.of(budget));
        when(budgetRepository.sumSpentInCents(any(), any(), any(), any())).thenReturn(0L);

        List<BudgetStatusResponse> result = useCase.execute(userId);

        LocalDate start = result.get(0).startDate();
        assertThat(result.get(0).endDate()).isEqualTo(start.plusYears(1).minusDays(1));
    }
}
