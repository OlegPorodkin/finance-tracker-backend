package com.financetracker.analytics.domain;

import com.financetracker.shared.domain.UserId;
import com.financetracker.transactions.domain.TransactionType;

import java.time.LocalDate;
import java.util.List;

public interface AnalyticsRepository {

    long sumByType(UserId userId, TransactionType type, LocalDate from, LocalDate to);

    List<CategoryAmount> sumByCategoryAndType(UserId userId, TransactionType type, LocalDate from, LocalDate to);

    List<MonthlyTotal> sumByMonth(UserId userId, int year);
}
