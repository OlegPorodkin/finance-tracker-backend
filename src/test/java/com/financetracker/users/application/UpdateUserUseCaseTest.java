package com.financetracker.users.application;

import com.financetracker.shared.domain.TransactionPort;
import com.financetracker.shared.domain.User;
import com.financetracker.shared.domain.UserId;
import com.financetracker.shared.domain.exception.NotFoundException;
import com.financetracker.users.application.dto.UpdateUserRequest;
import com.financetracker.users.application.dto.UserResponse;
import com.financetracker.users.domain.UserProfileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UpdateUserUseCaseTest {

    @Mock UserProfileRepository userProfileRepository;
    @Mock TransactionPort transactionPort;

    UpdateUserUseCase useCase;
    UserId userId = UserId.generate();

    @BeforeEach
    void setUp() {
        useCase = new UpdateUserUseCase(userProfileRepository, transactionPort);
        when(transactionPort.execute(any(Supplier.class)))
                .thenAnswer(inv -> inv.<Supplier<?>>getArgument(0).get());
    }

    @Test
    void updates_name_and_currency() {
        User user = User.create("bob@example.com", "hash", "Bob", "USD");
        when(userProfileRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userProfileRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        UserResponse result = useCase.execute(userId, new UpdateUserRequest("Bobby", "EUR"));

        assertThat(result.name()).isEqualTo("Bobby");
        assertThat(result.currency()).isEqualTo("EUR");
    }

    @Test
    void throws_not_found_when_user_does_not_exist() {
        when(userProfileRepository.findById(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(userId, new UpdateUserRequest("Name", "USD")))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("User not found");
    }
}
