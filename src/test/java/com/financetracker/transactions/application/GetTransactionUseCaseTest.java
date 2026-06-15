package com.financetracker.transactions.application;

import com.financetracker.shared.domain.Money;
import com.financetracker.shared.domain.UserId;
import com.financetracker.shared.domain.exception.NotFoundException;
import com.financetracker.transactions.application.dto.TransactionResponse;
import com.financetracker.transactions.domain.Transaction;
import com.financetracker.transactions.domain.TransactionRepository;
import com.financetracker.transactions.domain.TransactionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetTransactionUseCaseTest {

    @Mock TransactionRepository transactionRepository;

    GetTransactionUseCase useCase;
    UserId userId = UserId.generate();

    @BeforeEach
    void setUp() {
        useCase = new GetTransactionUseCase(transactionRepository);
    }

    @Test
    void returns_transaction_when_found() {
        Transaction t = Transaction.create(userId, TransactionType.EXPENSE,
                new Money(100_00, "USD"), LocalDate.of(2026, 1, 1),
                "Coffee", null, null);
        when(transactionRepository.findById("tx-1", userId)).thenReturn(Optional.of(t));

        TransactionResponse result = useCase.execute("tx-1", userId);

        assertThat(result.description()).isEqualTo("Coffee");
        assertThat(result.amountInCents()).isEqualTo(100_00);
        assertThat(result.type()).isEqualTo("EXPENSE");
    }

    @Test
    void throws_not_found_when_transaction_does_not_exist() {
        when(transactionRepository.findById("missing", userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute("missing", userId))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Transaction not found");
    }
}
