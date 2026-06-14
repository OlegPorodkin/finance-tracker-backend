package com.financetracker.users.application;

import com.financetracker.shared.domain.UserId;
import com.financetracker.shared.domain.exception.NotFoundException;
import com.financetracker.users.application.dto.UserResponse;
import com.financetracker.users.domain.UserProfileRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class GetUserProfileUseCase {

    private final UserProfileRepository userProfileRepository;

    public UserResponse execute(UserId userId) {
        return userProfileRepository.findById(userId)
                .map(UserResponse::from)
                .orElseThrow(() -> new NotFoundException("User not found"));
    }
}
