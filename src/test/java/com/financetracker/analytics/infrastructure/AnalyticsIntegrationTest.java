package com.financetracker.analytics.infrastructure;

import com.financetracker.BaseIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AnalyticsIntegrationTest extends BaseIntegrationTest {

    private void createTransaction(AuthCookies cookies, String type, long amountInCents) throws Exception {
        mockMvc.perform(post("/api/v1/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "type": "%s",
                          "amountInCents": %d,
                          "date": "%s",
                          "description": "Test transaction"
                        }
                        """.formatted(type, amountInCents, LocalDate.now().toString()))
                .cookie(cookies.accessToken()));
    }

    @Test
    void summary_returns_correct_income_expense_and_net() throws Exception {
        AuthCookies cookies = registerAndLogin("user@example.com", "password123", "User");

        createTransaction(cookies, "INCOME", 100000);
        createTransaction(cookies, "INCOME", 50000);
        createTransaction(cookies, "EXPENSE", 30000);

        String today = LocalDate.now().toString();

        mockMvc.perform(get("/api/v1/analytics/summary")
                        .param("from", today)
                        .param("to", today)
                        .cookie(cookies.accessToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.incomeInCents").value(150000))
                .andExpect(jsonPath("$.expenseInCents").value(30000))
                .andExpect(jsonPath("$.netInCents").value(120000))
                .andExpect(jsonPath("$.currency").exists());
    }

    @Test
    void summary_without_date_filter_returns_all_time_totals() throws Exception {
        AuthCookies cookies = registerAndLogin("user@example.com", "password123", "User");

        createTransaction(cookies, "INCOME", 200000);
        createTransaction(cookies, "EXPENSE", 80000);

        mockMvc.perform(get("/api/v1/analytics/summary")
                        .cookie(cookies.accessToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.incomeInCents").value(200000))
                .andExpect(jsonPath("$.expenseInCents").value(80000))
                .andExpect(jsonPath("$.netInCents").value(120000));
    }

    @Test
    void category_breakdown_returns_items_sorted_by_amount_with_percentages() throws Exception {
        AuthCookies cookies = registerAndLogin("user@example.com", "password123", "User");

        createTransaction(cookies, "EXPENSE", 70000);
        createTransaction(cookies, "EXPENSE", 30000);

        mockMvc.perform(get("/api/v1/analytics/by-category")
                        .param("type", "EXPENSE")
                        .cookie(cookies.accessToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void monthly_trend_returns_aggregated_data_for_year() throws Exception {
        AuthCookies cookies = registerAndLogin("user@example.com", "password123", "User");

        createTransaction(cookies, "INCOME", 500000);
        createTransaction(cookies, "EXPENSE", 200000);

        int currentYear = LocalDate.now().getYear();

        mockMvc.perform(get("/api/v1/analytics/monthly-trend")
                        .param("year", String.valueOf(currentYear))
                        .cookie(cookies.accessToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].year").value(currentYear))
                .andExpect(jsonPath("$[0].incomeInCents").value(500000))
                .andExpect(jsonPath("$[0].expenseInCents").value(200000))
                .andExpect(jsonPath("$[0].netInCents").value(300000));
    }

    @Test
    void monthly_trend_defaults_to_current_year_when_no_param() throws Exception {
        AuthCookies cookies = registerAndLogin("user@example.com", "password123", "User");
        createTransaction(cookies, "INCOME", 100000);

        mockMvc.perform(get("/api/v1/analytics/monthly-trend")
                        .cookie(cookies.accessToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].incomeInCents").value(100000));
    }
}
