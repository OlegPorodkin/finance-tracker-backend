package com.financetracker.budgets.infrastructure;

import com.financetracker.BaseIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class BudgetIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // Fetches the UUID of a default category by name (seeded by Liquibase)
    private String defaultCategoryId(String name) {
        return jdbcTemplate.queryForObject(
                "SELECT id::text FROM categories WHERE name = ? AND user_id IS NULL",
                String.class, name);
    }

    private String createBudget(AuthCookies cookies, String categoryId, String period) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/v1/budgets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "categoryId": "%s",
                                  "limitAmountInCents": 100000,
                                  "period": "%s",
                                  "alertThreshold": 80
                                }
                                """.formatted(categoryId, period))
                        .cookie(cookies.accessToken()))
                .andExpect(status().isCreated())
                .andReturn();
        return objectMapper.readTree(result.getResponse().getContentAsString()).get("id").asText();
    }

    @Test
    void create_budget_returns_201_with_correct_start_date() throws Exception {
        AuthCookies cookies = registerAndLogin("user@example.com", "password123", "User");
        String categoryId = defaultCategoryId("Housing");

        mockMvc.perform(post("/api/v1/budgets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "categoryId": "%s",
                                  "limitAmountInCents": 200000,
                                  "period": "MONTHLY",
                                  "alertThreshold": 75
                                }
                                """.formatted(categoryId))
                        .cookie(cookies.accessToken()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.period").value("MONTHLY"))
                .andExpect(jsonPath("$.limitAmountInCents").value(200000))
                .andExpect(jsonPath("$.alertThreshold").value(75))
                .andExpect(jsonPath("$.startDate").value(LocalDate.now().withDayOfMonth(1).toString()));
    }

    @Test
    void duplicate_budget_returns_400() throws Exception {
        AuthCookies cookies = registerAndLogin("user@example.com", "password123", "User");
        String categoryId = defaultCategoryId("Housing");

        createBudget(cookies, categoryId, "MONTHLY");

        mockMvc.perform(post("/api/v1/budgets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "categoryId": "%s",
                                  "limitAmountInCents": 50000,
                                  "period": "MONTHLY",
                                  "alertThreshold": 80
                                }
                                """.formatted(categoryId))
                        .cookie(cookies.accessToken()))
                .andExpect(status().isBadRequest());
    }

    @Test
    void budget_status_calculates_spent_and_percentage_correctly() throws Exception {
        AuthCookies cookies = registerAndLogin("user@example.com", "password123", "User");
        String categoryId = defaultCategoryId("Food & Groceries");

        createBudget(cookies, categoryId, "MONTHLY");

        // Create an EXPENSE transaction for this category
        mockMvc.perform(post("/api/v1/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "type": "EXPENSE",
                                  "amountInCents": 30000,
                                  "date": "%s",
                                  "description": "Groceries",
                                  "categoryId": "%s"
                                }
                                """.formatted(LocalDate.now().toString(), categoryId))
                        .cookie(cookies.accessToken()))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/v1/budgets/status")
                        .cookie(cookies.accessToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].spentInCents").value(30000))
                .andExpect(jsonPath("$[0].limitAmountInCents").value(100000))
                .andExpect(jsonPath("$[0].spentPercentage").value(30))
                .andExpect(jsonPath("$[0].alertTriggered").value(false));
    }

    @Test
    void update_budget_returns_200_with_new_limit() throws Exception {
        AuthCookies cookies = registerAndLogin("user@example.com", "password123", "User");
        String budgetId = createBudget(cookies, defaultCategoryId("Transport"), "MONTHLY");

        mockMvc.perform(put("/api/v1/budgets/{id}", budgetId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"limitAmountInCents": 50000, "alertThreshold": 90}
                                """)
                        .cookie(cookies.accessToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.limitAmountInCents").value(50000))
                .andExpect(jsonPath("$.alertThreshold").value(90));
    }

    @Test
    void delete_budget_returns_204() throws Exception {
        AuthCookies cookies = registerAndLogin("user@example.com", "password123", "User");
        String budgetId = createBudget(cookies, defaultCategoryId("Health"), "MONTHLY");

        mockMvc.perform(delete("/api/v1/budgets/{id}", budgetId)
                        .cookie(cookies.accessToken()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/v1/budgets")
                        .cookie(cookies.accessToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }
}
