package com.financetracker.analytics.infrastructure;

import com.financetracker.analytics.application.GetCategoryBreakdownUseCase;
import com.financetracker.analytics.application.GetMonthlyTrendUseCase;
import com.financetracker.analytics.application.GetSummaryUseCase;
import com.financetracker.analytics.domain.AnalyticsRepository;
import com.financetracker.users.domain.UserProfileRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AnalyticsUseCaseConfig {

    @Bean
    public GetSummaryUseCase getSummaryUseCase(AnalyticsRepository analyticsRepository,
                                                UserProfileRepository userProfileRepository) {
        return new GetSummaryUseCase(analyticsRepository, userProfileRepository);
    }

    @Bean
    public GetCategoryBreakdownUseCase getCategoryBreakdownUseCase(AnalyticsRepository analyticsRepository) {
        return new GetCategoryBreakdownUseCase(analyticsRepository);
    }

    @Bean
    public GetMonthlyTrendUseCase getMonthlyTrendUseCase(AnalyticsRepository analyticsRepository) {
        return new GetMonthlyTrendUseCase(analyticsRepository);
    }
}
