package com.financetracker.transactions.application;

import com.financetracker.shared.domain.UserId;
import com.financetracker.transactions.domain.Transaction;
import com.financetracker.transactions.domain.TransactionRepository;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.StringJoiner;

@RequiredArgsConstructor
public class ExportTransactionsUseCase {

    private final TransactionRepository transactionRepository;

    public String execute(UserId userId, LocalDate from, LocalDate to) {
        List<Transaction> transactions = transactionRepository.findAllForExport(userId, from, to);
        StringBuilder csv = new StringBuilder();
        csv.append("id,type,amountInCents,currency,date,description,categoryId,notes,createdAt\n");
        for (Transaction t : transactions) {
            csv.append(csvRow(
                    t.getId(),
                    t.getType().name(),
                    String.valueOf(t.getAmount().amountInCents()),
                    t.getAmount().currency(),
                    t.getDate().toString(),
                    escapeCsv(t.getDescription()),
                    t.getCategoryId() != null ? t.getCategoryId() : "",
                    t.getNotes() != null ? escapeCsv(t.getNotes()) : "",
                    t.getCreatedAt().toString()
            )).append('\n');
        }
        return csv.toString();
    }

    private String csvRow(String... fields) {
        StringJoiner joiner = new StringJoiner(",");
        for (String field : fields) joiner.add(field);
        return joiner.toString();
    }

    private String escapeCsv(String value) {
        if (value == null) return "";
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
}
