package com.financetracker.budgets.domain;

import com.financetracker.shared.domain.UserId;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface BudgetRepository {

    List<Budget> findAllByUserId(UserId userId);

    Optional<Budget> findById(String id, UserId userId);

    boolean existsByUserIdAndCategoryIdAndPeriod(UserId userId, String categoryId, BudgetPeriod period);

    Budget save(Budget budget);

    void delete(String id, UserId userId);

    // Queries transactions table to sum EXPENSE amounts for the given category in the date range
    long sumSpentInCents(UserId userId, String categoryId, LocalDate from, LocalDate to);
}
