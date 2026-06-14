package com.financetracker.users.application;

import com.financetracker.shared.domain.TransactionPort;
import com.financetracker.shared.domain.User;
import com.financetracker.shared.domain.UserId;
import com.financetracker.shared.domain.exception.NotFoundException;
import com.financetracker.users.application.dto.UpdateUserRequest;
import com.financetracker.users.application.dto.UserResponse;
import com.financetracker.users.domain.UserProfileRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UpdateUserUseCase {

    private final UserProfileRepository userProfileRepository;
    private final TransactionPort transactionPort;

    public UserResponse execute(UserId userId, UpdateUserRequest request) {
        return transactionPort.execute(() -> {
            User user = userProfileRepository.findById(userId)
                    .orElseThrow(() -> new NotFoundException("User not found"));
            user.updateProfile(request.name(), request.currency());
            return UserResponse.from(userProfileRepository.save(user));
        });
    }
}
