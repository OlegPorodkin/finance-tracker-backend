package com.financetracker.users.application;

import com.financetracker.shared.domain.User;
import com.financetracker.shared.domain.UserId;
import com.financetracker.shared.domain.exception.NotFoundException;
import com.financetracker.users.application.dto.UserResponse;
import com.financetracker.users.domain.UserProfileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetUserProfileUseCaseTest {

    @Mock UserProfileRepository userProfileRepository;

    GetUserProfileUseCase useCase;
    UserId userId = UserId.generate();

    @BeforeEach
    void setUp() {
        useCase = new GetUserProfileUseCase(userProfileRepository);
    }

    @Test
    void returns_user_response_when_user_exists() {
        User user = User.create("alice@example.com", "hash", "Alice", "USD");
        when(userProfileRepository.findById(userId)).thenReturn(Optional.of(user));

        UserResponse result = useCase.execute(userId);

        assertThat(result.email()).isEqualTo("alice@example.com");
        assertThat(result.name()).isEqualTo("Alice");
        assertThat(result.currency()).isEqualTo("USD");
    }

    @Test
    void throws_not_found_when_user_does_not_exist() {
        when(userProfileRepository.findById(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(userId))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("User not found");
    }
}
