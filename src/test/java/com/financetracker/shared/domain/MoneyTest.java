package com.financetracker.shared.domain;

import com.financetracker.shared.domain.exception.ValidationException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MoneyTest {

    @Test
    void rejects_negative_amount() {
        assertThatThrownBy(() -> new Money(-1, "USD"))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("negative");
    }

    @Test
    void rejects_blank_currency() {
        assertThatThrownBy(() -> new Money(100, "  "))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Currency");
    }

    @Test
    void rejects_null_currency() {
        assertThatThrownBy(() -> new Money(100, null))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    void add_sums_amounts_in_same_currency() {
        Money a = new Money(300_00, "USD");
        Money b = new Money(200_00, "USD");

        assertThat(a.add(b).amountInCents()).isEqualTo(500_00);
        assertThat(a.add(b).currency()).isEqualTo("USD");
    }

    @Test
    void subtract_returns_difference_in_same_currency() {
        Money a = new Money(300_00, "USD");
        Money b = new Money(100_00, "USD");

        assertThat(a.subtract(b).amountInCents()).isEqualTo(200_00);
    }

    @Test
    void add_throws_when_currencies_differ() {
        Money usd = new Money(100_00, "USD");
        Money eur = new Money(100_00, "EUR");

        assertThatThrownBy(() -> usd.add(eur))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("currencies");
    }

    @Test
    void subtract_throws_when_currencies_differ() {
        Money usd = new Money(100_00, "USD");
        Money eur = new Money(50_00, "EUR");

        assertThatThrownBy(() -> usd.subtract(eur))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    void zero_amount_is_valid() {
        Money m = new Money(0, "USD");
        assertThat(m.amountInCents()).isZero();
    }
}
