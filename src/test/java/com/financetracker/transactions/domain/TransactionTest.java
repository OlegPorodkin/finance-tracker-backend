package com.financetracker.transactions.domain;

import com.financetracker.shared.domain.Money;
import com.financetracker.shared.domain.UserId;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TransactionTest {

    private final UserId userId = UserId.generate();

    @Test
    void create_sets_all_fields() {
        Money amount = new Money(500_00, "USD");
        LocalDate date = LocalDate.of(2026, 1, 15);

        Transaction t = Transaction.create(userId, TransactionType.EXPENSE, amount, date, "Groceries", "cat-1", "weekly shop");

        assertThat(t.getUserId()).isEqualTo(userId);
        assertThat(t.getType()).isEqualTo(TransactionType.EXPENSE);
        assertThat(t.getAmount()).isEqualTo(amount);
        assertThat(t.getDate()).isEqualTo(date);
        assertThat(t.getDescription()).isEqualTo("Groceries");
        assertThat(t.getCategoryId()).isEqualTo("cat-1");
        assertThat(t.getNotes()).isEqualTo("weekly shop");
        assertThat(t.getId()).isNotNull();
    }

    @Test
    void update_changes_mutable_fields() {
        Transaction t = Transaction.create(userId, TransactionType.INCOME, new Money(100_00, "USD"),
                LocalDate.of(2026, 1, 1), "Salary", null, null);

        Money newAmount = new Money(200_00, "USD");
        LocalDate newDate = LocalDate.of(2026, 2, 1);
        t.update(newAmount, newDate, "Bonus", "cat-2", "Q1 bonus");

        assertThat(t.getAmount()).isEqualTo(newAmount);
        assertThat(t.getDate()).isEqualTo(newDate);
        assertThat(t.getDescription()).isEqualTo("Bonus");
        assertThat(t.getCategoryId()).isEqualTo("cat-2");
        assertThat(t.getNotes()).isEqualTo("Q1 bonus");
    }

    @Test
    void update_does_not_change_type() {
        Transaction t = Transaction.create(userId, TransactionType.INCOME, new Money(100_00, "USD"),
                LocalDate.of(2026, 1, 1), "Salary", null, null);

        t.update(new Money(200_00, "USD"), LocalDate.of(2026, 2, 1), "Salary", null, null);

        assertThat(t.getType()).isEqualTo(TransactionType.INCOME);
    }

    @Test
    void update_throws_when_amount_is_null() {
        Transaction t = Transaction.create(userId, TransactionType.EXPENSE, new Money(100_00, "USD"),
                LocalDate.of(2026, 1, 1), "Desc", null, null);

        assertThatThrownBy(() -> t.update(null, LocalDate.of(2026, 1, 1), "Desc", null, null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void update_throws_when_date_is_null() {
        Transaction t = Transaction.create(userId, TransactionType.EXPENSE, new Money(100_00, "USD"),
                LocalDate.of(2026, 1, 1), "Desc", null, null);

        assertThatThrownBy(() -> t.update(new Money(100_00, "USD"), null, "Desc", null, null))
                .isInstanceOf(NullPointerException.class);
    }
}
