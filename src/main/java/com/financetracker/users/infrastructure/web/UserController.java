package com.financetracker.users.infrastructure.web;

import com.financetracker.shared.domain.UserId;
import com.financetracker.users.application.GetUserProfileUseCase;
import com.financetracker.users.application.UpdateUserUseCase;
import com.financetracker.users.application.dto.UpdateUserRequest;
import com.financetracker.users.application.dto.UserResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final GetUserProfileUseCase getUserProfileUseCase;
    private final UpdateUserUseCase updateUserUseCase;

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getProfile(@AuthenticationPrincipal UserId userId) {
        return ResponseEntity.ok(getUserProfileUseCase.execute(userId));
    }

    @PutMapping("/me")
    public ResponseEntity<UserResponse> updateProfile(@AuthenticationPrincipal UserId userId,
                                                       @Valid @RequestBody UpdateUserRequest request) {
        return ResponseEntity.ok(updateUserUseCase.execute(userId, request));
    }
}
