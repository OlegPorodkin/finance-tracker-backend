package com.financetracker.budgets.domain;

import com.financetracker.shared.domain.UserId;
import lombok.Getter;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Getter
public class Budget {

    private final String id;
    private final UserId userId;
    private final String categoryId;
    private long limitAmountInCents;
    private final BudgetPeriod period;
    private final LocalDate startDate;
    private int alertThreshold;
    private final Instant createdAt;
    private Instant updatedAt;

    public Budget(String id, UserId userId, String categoryId, long limitAmountInCents,
                  BudgetPeriod period, LocalDate startDate, int alertThreshold,
                  Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.userId = userId;
        this.categoryId = categoryId;
        this.limitAmountInCents = limitAmountInCents;
        this.period = period;
        this.startDate = startDate;
        this.alertThreshold = alertThreshold;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static Budget create(UserId userId, String categoryId, long limitAmountInCents,
                                BudgetPeriod period, int alertThreshold) {
        Instant now = Instant.now();
        return new Budget(UUID.randomUUID().toString(), userId, categoryId, limitAmountInCents,
                period, startOfCurrentPeriod(period), alertThreshold, now, now);
    }

    public void update(long limitAmountInCents, int alertThreshold) {
        this.limitAmountInCents = limitAmountInCents;
        this.alertThreshold = alertThreshold;
        this.updatedAt = Instant.now();
    }

    private static LocalDate startOfCurrentPeriod(BudgetPeriod period) {
        LocalDate today = LocalDate.now();
        return switch (period) {
            case WEEKLY -> today.with(DayOfWeek.MONDAY);
            case MONTHLY -> today.withDayOfMonth(1);
            case YEARLY -> today.withDayOfYear(1);
        };
    }
}
