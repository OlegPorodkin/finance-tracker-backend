package com.financetracker.auth.infrastructure.web;

import com.financetracker.auth.application.LoginUseCase;
import com.financetracker.auth.application.LogoutUseCase;
import com.financetracker.auth.application.RefreshTokenUseCase;
import com.financetracker.auth.application.RegisterUserUseCase;
import com.financetracker.auth.application.dto.AuthResponse;
import com.financetracker.auth.application.dto.AuthResult;
import com.financetracker.auth.application.dto.LoginRequest;
import com.financetracker.auth.application.dto.RegisterRequest;
import com.financetracker.shared.domain.exception.UnauthorizedException;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final RegisterUserUseCase registerUserUseCase;
    private final LoginUseCase loginUseCase;
    private final RefreshTokenUseCase refreshTokenUseCase;
    private final LogoutUseCase logoutUseCase;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request,
                                                  HttpServletResponse response) {
        AuthResult result = registerUserUseCase.execute(request);
        setAuthCookies(response, result.accessToken(), result.refreshToken());
        return ResponseEntity.status(HttpStatus.CREATED).body(AuthResponse.from(result.user()));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request,
                                               HttpServletResponse response) {
        AuthResult result = loginUseCase.execute(request);
        setAuthCookies(response, result.accessToken(), result.refreshToken());
        return ResponseEntity.ok(AuthResponse.from(result.user()));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(
            @CookieValue(value = "refreshToken", required = false) String rawToken,
            HttpServletResponse response) {
        if (rawToken == null) {
            throw new UnauthorizedException("Refresh token not provided");
        }
        AuthResult result = refreshTokenUseCase.execute(rawToken);
        setAuthCookies(response, result.accessToken(), result.refreshToken());
        return ResponseEntity.ok(AuthResponse.from(result.user()));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @CookieValue(value = "refreshToken", required = false) String rawToken,
            HttpServletResponse response) {
        if (rawToken != null) {
            logoutUseCase.execute(rawToken);
        }
        clearCookies(response);
        return ResponseEntity.noContent().build();
    }

    private void setAuthCookies(HttpServletResponse response, String accessToken, String refreshToken) {
        response.addHeader(HttpHeaders.SET_COOKIE, ResponseCookie.from("accessToken", accessToken)
                .httpOnly(true).secure(true).sameSite("Strict")
                .maxAge(Duration.ofMinutes(15)).path("/")
                .build().toString());
        response.addHeader(HttpHeaders.SET_COOKIE, ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true).secure(true).sameSite("Strict")
                .maxAge(Duration.ofDays(7)).path("/api/v1/auth/")
                .build().toString());
    }

    private void clearCookies(HttpServletResponse response) {
        response.addHeader(HttpHeaders.SET_COOKIE, ResponseCookie.from("accessToken", "")
                .httpOnly(true).secure(true).sameSite("Strict")
                .maxAge(0).path("/")
                .build().toString());
        response.addHeader(HttpHeaders.SET_COOKIE, ResponseCookie.from("refreshToken", "")
                .httpOnly(true).secure(true).sameSite("Strict")
                .maxAge(0).path("/api/v1/auth/")
                .build().toString());
    }
}
