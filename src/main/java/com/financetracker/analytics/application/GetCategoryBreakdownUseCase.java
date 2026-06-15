package com.financetracker.analytics.application;

import com.financetracker.analytics.application.dto.CategoryBreakdownResponse;
import com.financetracker.analytics.domain.AnalyticsRepository;
import com.financetracker.analytics.domain.CategoryAmount;
import com.financetracker.shared.domain.UserId;
import com.financetracker.transactions.domain.TransactionType;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
public class GetCategoryBreakdownUseCase {

    private final AnalyticsRepository analyticsRepository;

    public List<CategoryBreakdownResponse> execute(UserId userId, TransactionType type,
                                                    LocalDate from, LocalDate to) {
        List<CategoryAmount> amounts = analyticsRepository.sumByCategoryAndType(userId, type, from, to);
        long total = amounts.stream().mapToLong(CategoryAmount::amountInCents).sum();
        return amounts.stream()
                .map(a -> new CategoryBreakdownResponse(
                        a.categoryId(),
                        a.categoryName(),
                        a.amountInCents(),
                        total > 0 ? (int) Math.round((a.amountInCents() * 100.0) / total) : 0
                ))
                .toList();
    }
}
