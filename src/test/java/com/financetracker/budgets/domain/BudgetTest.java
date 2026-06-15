package com.financetracker.budgets.domain;

import com.financetracker.shared.domain.UserId;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class BudgetTest {

    private final UserId userId = UserId.generate();

    @Test
    void create_sets_monthly_start_to_first_of_current_month() {
        Budget budget = Budget.create(userId, null, 100_00L, BudgetPeriod.MONTHLY, 80);

        assertThat(budget.getStartDate()).isEqualTo(LocalDate.now().withDayOfMonth(1));
    }

    @Test
    void create_sets_weekly_start_to_monday_of_current_week() {
        Budget budget = Budget.create(userId, null, 100_00L, BudgetPeriod.WEEKLY, 80);

        assertThat(budget.getStartDate().getDayOfWeek()).isEqualTo(DayOfWeek.MONDAY);
        assertThat(budget.getStartDate()).isBeforeOrEqualTo(LocalDate.now());
    }

    @Test
    void create_sets_yearly_start_to_first_of_current_year() {
        Budget budget = Budget.create(userId, null, 100_00L, BudgetPeriod.YEARLY, 80);

        assertThat(budget.getStartDate()).isEqualTo(LocalDate.now().withDayOfYear(1));
    }

    @Test
    void update_changes_limit_and_threshold() {
        Budget budget = Budget.create(userId, null, 100_00L, BudgetPeriod.MONTHLY, 80);

        budget.update(200_00L, 90);

        assertThat(budget.getLimitAmountInCents()).isEqualTo(200_00L);
        assertThat(budget.getAlertThreshold()).isEqualTo(90);
    }
}
