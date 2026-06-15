package com.financetracker.analytics.infrastructure.web;

import com.financetracker.analytics.application.GetCategoryBreakdownUseCase;
import com.financetracker.analytics.application.GetMonthlyTrendUseCase;
import com.financetracker.analytics.application.GetSummaryUseCase;
import com.financetracker.analytics.application.dto.CategoryBreakdownResponse;
import com.financetracker.analytics.application.dto.MonthlyTrendResponse;
import com.financetracker.analytics.application.dto.SummaryResponse;
import com.financetracker.shared.domain.UserId;
import com.financetracker.transactions.domain.TransactionType;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/analytics")
@RequiredArgsConstructor
public class AnalyticsController {

    private final GetSummaryUseCase getSummaryUseCase;
    private final GetCategoryBreakdownUseCase getCategoryBreakdownUseCase;
    private final GetMonthlyTrendUseCase getMonthlyTrendUseCase;

    @GetMapping("/summary")
    public ResponseEntity<SummaryResponse> getSummary(
            @AuthenticationPrincipal UserId userId,
            @RequestParam(required = false) LocalDate from,
            @RequestParam(required = false) LocalDate to) {
        return ResponseEntity.ok(getSummaryUseCase.execute(userId, from, to));
    }

    @GetMapping("/by-category")
    public ResponseEntity<List<CategoryBreakdownResponse>> getCategoryBreakdown(
            @AuthenticationPrincipal UserId userId,
            @RequestParam(required = false) LocalDate from,
            @RequestParam(required = false) LocalDate to,
            @RequestParam(defaultValue = "EXPENSE") TransactionType type) {
        return ResponseEntity.ok(getCategoryBreakdownUseCase.execute(userId, type, from, to));
    }

    @GetMapping("/monthly-trend")
    public ResponseEntity<List<MonthlyTrendResponse>> getMonthlyTrend(
            @AuthenticationPrincipal UserId userId,
            @RequestParam(required = false) Integer year) {
        int targetYear = year != null ? year : LocalDate.now().getYear();
        return ResponseEntity.ok(getMonthlyTrendUseCase.execute(userId, targetYear));
    }
}
