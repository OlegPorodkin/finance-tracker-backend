package com.financetracker.transactions.application;

import com.financetracker.shared.domain.Money;
import com.financetracker.shared.domain.TransactionPort;
import com.financetracker.shared.domain.User;
import com.financetracker.shared.domain.UserId;
import com.financetracker.shared.domain.exception.NotFoundException;
import com.financetracker.transactions.application.dto.TransactionResponse;
import com.financetracker.transactions.application.dto.UpdateTransactionRequest;
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
class UpdateTransactionUseCaseTest {

    @Mock TransactionRepository transactionRepository;
    @Mock UserProfileRepository userProfileRepository;
    @Mock TransactionPort transactionPort;

    UpdateTransactionUseCase useCase;
    UserId userId = UserId.generate();

    @BeforeEach
    void setUp() {
        useCase = new UpdateTransactionUseCase(transactionRepository, userProfileRepository, transactionPort);
        when(transactionPort.execute(any(Supplier.class)))
                .thenAnswer(inv -> inv.<Supplier<?>>getArgument(0).get());
    }

    @Test
    void updates_transaction_with_user_currency() {
        Transaction existing = Transaction.create(userId, TransactionType.EXPENSE,
                new Money(50_00, "USD"), LocalDate.of(2026, 1, 1), "Old", null, null);
        User user = User.create("u@example.com", "hash", "User", "USD");
        when(transactionRepository.findById("tx-1", userId)).thenReturn(Optional.of(existing));
        when(userProfileRepository.findById(userId)).thenReturn(Optional.of(user));
        when(transactionRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        TransactionResponse result = useCase.execute("tx-1", userId,
                new UpdateTransactionRequest(200_00L, LocalDate.of(2026, 2, 1), "New", "cat-1", null));

        assertThat(result.amountInCents()).isEqualTo(200_00L);
        assertThat(result.description()).isEqualTo("New");
        assertThat(result.currency()).isEqualTo("USD");
    }

    @Test
    void throws_not_found_when_transaction_does_not_exist() {
        when(transactionRepository.findById("missing", userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute("missing", userId,
                new UpdateTransactionRequest(100_00L, LocalDate.of(2026, 1, 1), "Desc", null, null)))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Transaction not found");
    }

    @Test
    void throws_not_found_when_user_does_not_exist() {
        Transaction existing = Transaction.create(userId, TransactionType.INCOME,
                new Money(100_00, "USD"), LocalDate.of(2026, 1, 1), "Salary", null, null);
        when(transactionRepository.findById("tx-1", userId)).thenReturn(Optional.of(existing));
        when(userProfileRepository.findById(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute("tx-1", userId,
                new UpdateTransactionRequest(100_00L, LocalDate.of(2026, 1, 1), "Salary", null, null)))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("User not found");
    }
}
