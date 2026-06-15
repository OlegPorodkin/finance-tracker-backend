package com.financetracker.transactions.application;

import com.financetracker.shared.domain.TransactionPort;
import com.financetracker.shared.domain.User;
import com.financetracker.shared.domain.UserId;
import com.financetracker.shared.domain.exception.NotFoundException;
import com.financetracker.transactions.application.dto.CreateTransactionRequest;
import com.financetracker.transactions.application.dto.TransactionResponse;
import com.financetracker.transactions.domain.Transaction;
import com.financetracker.transactions.domain.TransactionRepository;
import com.financetracker.transactions.domain.TransactionType;
import com.financetracker.users.domain.UserProfileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateTransactionUseCaseTest {

    @Mock TransactionRepository transactionRepository;
    @Mock UserProfileRepository userProfileRepository;
    @Mock TransactionPort transactionPort;

    CreateTransactionUseCase useCase;
    UserId userId = UserId.generate();

    @BeforeEach
    void setUp() {
        useCase = new CreateTransactionUseCase(transactionRepository, userProfileRepository, transactionPort);
        when(transactionPort.execute(any(Supplier.class)))
                .thenAnswer(inv -> inv.<Supplier<?>>getArgument(0).get());
    }

    @Test
    void creates_transaction_using_user_currency() {
        User user = User.create("alice@example.com", "hash", "Alice", "EUR");
        when(userProfileRepository.findById(userId)).thenReturn(Optional.of(user));
        when(transactionRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        CreateTransactionRequest request = new CreateTransactionRequest(
                TransactionType.EXPENSE, 75_00L, LocalDate.of(2026, 1, 10), "Lunch", null, null);

        TransactionResponse result = useCase.execute(userId, request);

        assertThat(result.currency()).isEqualTo("EUR");
        assertThat(result.amountInCents()).isEqualTo(75_00L);
        assertThat(result.type()).isEqualTo("EXPENSE");
    }

    @Test
    void throws_not_found_when_user_does_not_exist() {
        when(userProfileRepository.findById(userId)).thenReturn(Optional.empty());

        CreateTransactionRequest request = new CreateTransactionRequest(
                TransactionType.INCOME, 100_00L, LocalDate.of(2026, 1, 1), "Salary", null, null);

        assertThatThrownBy(() -> useCase.execute(userId, request))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("User not found");
    }
}
