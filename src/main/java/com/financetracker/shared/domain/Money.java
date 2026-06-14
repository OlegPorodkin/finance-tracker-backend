package com.financetracker.shared.domain;

import com.financetracker.shared.domain.exception.ValidationException;

public record Money(long amountInCents, String currency) {

    public Money {
        if (amountInCents < 0) throw new ValidationException("Amount cannot be negative");
        if (currency == null || currency.isBlank()) throw new ValidationException("Currency required");
    }

    public Money add(Money other) {
        requireSameCurrency(other);
        return new Money(this.amountInCents + other.amountInCents, this.currency);
    }

    public Money subtract(Money other) {
        requireSameCurrency(other);
        return new Money(this.amountInCents - other.amountInCents, this.currency);
    }

    private void requireSameCurrency(Money other) {
        if (!this.currency.equals(other.currency)) {
            throw new ValidationException("Cannot operate on Money with different currencies: %s vs %s"
                    .formatted(this.currency, other.currency));
        }
    }
}
