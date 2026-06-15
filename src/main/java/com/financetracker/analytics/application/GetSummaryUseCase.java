package com.financetracker.analytics.application;

import com.financetracker.analytics.application.dto.SummaryResponse;
import com.financetracker.analytics.domain.AnalyticsRepository;
import com.financetracker.shared.domain.UserId;
import com.financetracker.shared.domain.exception.NotFoundException;
import com.financetracker.transactions.domain.TransactionType;
import com.financetracker.users.domain.UserProfileRepository;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;

@RequiredArgsConstructor
public class GetSummaryUseCase {

    private final AnalyticsRepository analyticsRepository;
    private final UserProfileRepository userProfileRepository;

    public SummaryResponse execute(UserId userId, LocalDate from, LocalDate to) {
        String currency = userProfileRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"))
                .getCurrency();
        long income = analyticsRepository.sumByType(userId, TransactionType.INCOME, from, to);
        long expense = analyticsRepository.sumByType(userId, TransactionType.EXPENSE, from, to);
        return new SummaryResponse(income, expense, income - expense, currency);
    }
}
