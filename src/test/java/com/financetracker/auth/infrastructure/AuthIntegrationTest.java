package com.financetracker.auth.infrastructure;

import com.financetracker.BaseIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AuthIntegrationTest extends BaseIntegrationTest {

    private static final String EMAIL = "test@example.com";
    private static final String PASSWORD = "password123";
    private static final String NAME = "Test User";

    @Test
    void register_returns_201_with_user_body_and_auth_cookies() throws Exception {
        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"%s","password":"%s","name":"%s"}
                                """.formatted(EMAIL, PASSWORD, NAME)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.user.email").value(EMAIL))
                .andExpect(jsonPath("$.user.name").value(NAME))
                .andExpect(cookie().exists("accessToken"))
                .andExpect(cookie().exists("refreshToken"))
                .andExpect(cookie().httpOnly("accessToken", true))
                .andExpect(cookie().httpOnly("refreshToken", true));
    }

    @Test
    void register_duplicate_email_returns_409() throws Exception {
        mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"email":"%s","password":"%s","name":"%s"}
                        """.formatted(EMAIL, PASSWORD, NAME)));

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"%s","password":"other123","name":"Other"}
                                """.formatted(EMAIL)))
                .andExpect(status().isConflict());
    }

    @Test
    void login_returns_200_with_auth_cookies() throws Exception {
        registerAndLogin(EMAIL, PASSWORD, NAME);

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"%s","password":"%s"}
                                """.formatted(EMAIL, PASSWORD)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user.email").value(EMAIL))
                .andExpect(cookie().exists("accessToken"))
                .andExpect(cookie().exists("refreshToken"));
    }

    @Test
    void login_wrong_password_returns_401() throws Exception {
        registerAndLogin(EMAIL, PASSWORD, NAME);

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"%s","password":"wrongpassword"}
                                """.formatted(EMAIL)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void refresh_token_rotation_issues_new_cookies() throws Exception {
        AuthCookies cookies = registerAndLogin(EMAIL, PASSWORD, NAME);

        MvcResult refreshResult = mockMvc.perform(post("/api/v1/auth/refresh")
                        .cookie(cookies.refreshToken()))
                .andExpect(status().isOk())
                .andExpect(cookie().exists("accessToken"))
                .andExpect(cookie().exists("refreshToken"))
                .andReturn();

        String newRefreshToken = refreshResult.getResponse().getCookie("refreshToken").getValue();
        assertThat(newRefreshToken).isNotEqualTo(cookies.refreshToken().getValue());
    }

    @Test
    void refresh_token_reuse_detection_revokes_all_sessions_and_returns_401() throws Exception {
        AuthCookies cookies = registerAndLogin(EMAIL, PASSWORD, NAME);

        // Use the refresh token once (rotates it)
        mockMvc.perform(post("/api/v1/auth/refresh")
                        .cookie(cookies.refreshToken()))
                .andExpect(status().isOk());

        // Reuse the old (now revoked) token → reuse detected → 401
        mockMvc.perform(post("/api/v1/auth/refresh")
                        .cookie(cookies.refreshToken()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void logout_clears_auth_cookies() throws Exception {
        AuthCookies cookies = registerAndLogin(EMAIL, PASSWORD, NAME);

        mockMvc.perform(post("/api/v1/auth/logout")
                        .cookie(cookies.accessToken())
                        .cookie(cookies.refreshToken()))
                .andExpect(status().isNoContent())
                .andExpect(cookie().maxAge("accessToken", 0))
                .andExpect(cookie().maxAge("refreshToken", 0));
    }
}
