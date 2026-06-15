package com.financetracker.transactions.infrastructure;

import com.financetracker.BaseIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class TransactionIntegrationTest extends BaseIntegrationTest {

    private static final String CREATE_BODY = """
            {
              "type": "EXPENSE",
              "amountInCents": 5000,
              "date": "2026-06-01",
              "description": "Groceries"
            }
            """;

    @Test
    void create_transaction_returns_201_with_body() throws Exception {
        AuthCookies cookies = registerAndLogin("user@example.com", "password123", "User");

        mockMvc.perform(post("/api/v1/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(CREATE_BODY)
                        .cookie(cookies.accessToken()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.type").value("EXPENSE"))
                .andExpect(jsonPath("$.amountInCents").value(5000))
                .andExpect(jsonPath("$.description").value("Groceries"))
                .andExpect(jsonPath("$.id").exists());
    }

    @Test
    void get_transaction_by_id_returns_200() throws Exception {
        AuthCookies cookies = registerAndLogin("user@example.com", "password123", "User");

        MvcResult created = mockMvc.perform(post("/api/v1/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(CREATE_BODY)
                        .cookie(cookies.accessToken()))
                .andReturn();

        String id = objectMapper.readTree(created.getResponse().getContentAsString()).get("id").asText();

        mockMvc.perform(get("/api/v1/transactions/{id}", id)
                        .cookie(cookies.accessToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id));
    }

    @Test
    void list_transactions_returns_paged_result() throws Exception {
        AuthCookies cookies = registerAndLogin("user@example.com", "password123", "User");

        for (int i = 0; i < 3; i++) {
            mockMvc.perform(post("/api/v1/transactions")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""
                            {"type":"EXPENSE","amountInCents":%d,"date":"2026-06-01","description":"Item %d"}
                            """.formatted(1000 * (i + 1), i))
                    .cookie(cookies.accessToken()));
        }

        mockMvc.perform(get("/api/v1/transactions")
                        .param("page", "0")
                        .param("size", "2")
                        .cookie(cookies.accessToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(3))
                .andExpect(jsonPath("$.totalPages").value(2))
                .andExpect(jsonPath("$.content.length()").value(2));
    }

    @Test
    void update_transaction_returns_200_with_updated_fields() throws Exception {
        AuthCookies cookies = registerAndLogin("user@example.com", "password123", "User");

        MvcResult created = mockMvc.perform(post("/api/v1/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(CREATE_BODY)
                        .cookie(cookies.accessToken()))
                .andReturn();

        String id = objectMapper.readTree(created.getResponse().getContentAsString()).get("id").asText();

        mockMvc.perform(put("/api/v1/transactions/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"amountInCents":9999,"date":"2026-06-15","description":"Updated"}
                                """)
                        .cookie(cookies.accessToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.amountInCents").value(9999))
                .andExpect(jsonPath("$.description").value("Updated"));
    }

    @Test
    void delete_transaction_returns_204_then_404_on_get() throws Exception {
        AuthCookies cookies = registerAndLogin("user@example.com", "password123", "User");

        MvcResult created = mockMvc.perform(post("/api/v1/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(CREATE_BODY)
                        .cookie(cookies.accessToken()))
                .andReturn();

        String id = objectMapper.readTree(created.getResponse().getContentAsString()).get("id").asText();

        mockMvc.perform(delete("/api/v1/transactions/{id}", id)
                        .cookie(cookies.accessToken()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/v1/transactions/{id}", id)
                        .cookie(cookies.accessToken()))
                .andExpect(status().isNotFound());
    }

    @Test
    void data_isolation_userB_cannot_see_userA_transactions() throws Exception {
        AuthCookies userA = registerAndLogin("userA@example.com", "password123", "User A");
        AuthCookies userB = registerAndLogin("userB@example.com", "password123", "User B");

        // userA creates a transaction
        mockMvc.perform(post("/api/v1/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(CREATE_BODY)
                .cookie(userA.accessToken()))
                .andExpect(status().isCreated());

        // userB sees an empty list
        mockMvc.perform(get("/api/v1/transactions")
                        .cookie(userB.accessToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(0))
                .andExpect(jsonPath("$.content").isEmpty());
    }

    @Test
    void unauthenticated_request_returns_401() throws Exception {
        mockMvc.perform(get("/api/v1/transactions"))
                .andExpect(status().isUnauthorized());
    }
}
