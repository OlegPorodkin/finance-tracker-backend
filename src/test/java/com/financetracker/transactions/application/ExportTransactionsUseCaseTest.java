package com.financetracker.transactions.application;

import com.financetracker.shared.domain.Money;
import com.financetracker.shared.domain.UserId;
import com.financetracker.transactions.domain.Transaction;
import com.financetracker.transactions.domain.TransactionRepository;
import com.financetracker.transactions.domain.TransactionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExportTransactionsUseCaseTest {

    @Mock TransactionRepository transactionRepository;

    ExportTransactionsUseCase useCase;
    UserId userId = UserId.generate();

    @BeforeEach
    void setUp() {
        useCase = new ExportTransactionsUseCase(transactionRepository);
    }

    @Test
    void export_includes_csv_header() {
        when(transactionRepository.findAllForExport(userId, null, null)).thenReturn(List.of());

        String csv = useCase.execute(userId, null, null);

        assertThat(csv).startsWith("id,type,amountInCents,currency,date,description,categoryId,notes,createdAt\n");
    }

    @Test
    void export_empty_list_returns_only_header() {
        when(transactionRepository.findAllForExport(userId, null, null)).thenReturn(List.of());

        String csv = useCase.execute(userId, null, null);

        assertThat(csv.lines().count()).isEqualTo(1);
    }

    @Test
    void export_serializes_transaction_fields() {
        Transaction t = Transaction.create(userId, TransactionType.EXPENSE,
                new Money(150_00, "USD"), LocalDate.of(2026, 1, 10),
                "Groceries", "cat-1", null);
        when(transactionRepository.findAllForExport(userId, null, null)).thenReturn(List.of(t));

        String csv = useCase.execute(userId, null, null);
        String[] lines = csv.split("\n");

        assertThat(lines).hasSize(2);
        assertThat(lines[1]).contains("EXPENSE");
        assertThat(lines[1]).contains("15000");
        assertThat(lines[1]).contains("USD");
        assertThat(lines[1]).contains("2026-01-10");
        assertThat(lines[1]).contains("Groceries");
    }

    @Test
    void export_escapes_commas_in_description() {
        Transaction t = Transaction.create(userId, TransactionType.EXPENSE,
                new Money(100_00, "USD"), LocalDate.of(2026, 1, 1),
                "Coffee, tea", null, null);
        when(transactionRepository.findAllForExport(userId, null, null)).thenReturn(List.of(t));

        String csv = useCase.execute(userId, null, null);
        String dataLine = csv.split("\n")[1];

        assertThat(dataLine).contains("\"Coffee, tea\"");
    }

    @Test
    void export_escapes_double_quotes_in_description() {
        Transaction t = Transaction.create(userId, TransactionType.INCOME,
                new Money(200_00, "USD"), LocalDate.of(2026, 1, 1),
                "Said \"hello\"", null, null);
        when(transactionRepository.findAllForExport(userId, null, null)).thenReturn(List.of(t));

        String csv = useCase.execute(userId, null, null);
        String dataLine = csv.split("\n")[1];

        assertThat(dataLine).contains("\"Said \"\"hello\"\"\"");
    }

    @Test
    void export_uses_empty_string_for_null_category() {
        Transaction t = Transaction.create(userId, TransactionType.INCOME,
                new Money(500_00, "USD"), LocalDate.of(2026, 1, 1),
                "Salary", null, null);
        when(transactionRepository.findAllForExport(userId, null, null)).thenReturn(List.of(t));

        String csv = useCase.execute(userId, null, null);
        String dataLine = csv.split("\n")[1];

        // categoryId and notes should both be empty (two consecutive commas)
        assertThat(dataLine).contains(",,");
    }
}
