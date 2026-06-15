package com.financetracker.analytics.application;

import com.financetracker.analytics.application.dto.MonthlyTrendResponse;
import com.financetracker.analytics.domain.AnalyticsRepository;
import com.financetracker.shared.domain.UserId;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
public class GetMonthlyTrendUseCase {

    private final AnalyticsRepository analyticsRepository;

    public List<MonthlyTrendResponse> execute(UserId userId, int year) {
        return analyticsRepository.sumByMonth(userId, year)
                .stream()
                .map(m -> new MonthlyTrendResponse(
                        m.year(),
                        m.month(),
                        m.incomeInCents(),
                        m.expenseInCents(),
                        m.incomeInCents() - m.expenseInCents()
                ))
                .toList();
    }
}
