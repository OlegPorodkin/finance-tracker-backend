package com.financetracker.transactions.infrastructure.web;

import com.financetracker.shared.domain.UserId;
import com.financetracker.shared.infrastructure.web.PageResponse;
import com.financetracker.transactions.application.CreateTransactionUseCase;
import com.financetracker.transactions.application.DeleteTransactionUseCase;
import com.financetracker.transactions.application.ExportTransactionsUseCase;
import com.financetracker.transactions.application.GetTransactionUseCase;
import com.financetracker.transactions.application.GetTransactionsUseCase;
import com.financetracker.transactions.application.UpdateTransactionUseCase;
import com.financetracker.transactions.application.dto.CreateTransactionRequest;
import com.financetracker.transactions.application.dto.TransactionResponse;
import com.financetracker.transactions.application.dto.UpdateTransactionRequest;
import com.financetracker.transactions.domain.TransactionFilter;
import com.financetracker.transactions.domain.TransactionType;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final GetTransactionsUseCase getTransactionsUseCase;
    private final GetTransactionUseCase getTransactionUseCase;
    private final CreateTransactionUseCase createTransactionUseCase;
    private final UpdateTransactionUseCase updateTransactionUseCase;
    private final DeleteTransactionUseCase deleteTransactionUseCase;
    private final ExportTransactionsUseCase exportTransactionsUseCase;

    @GetMapping
    public ResponseEntity<PageResponse<TransactionResponse>> getTransactions(
            @AuthenticationPrincipal UserId userId,
            @RequestParam(required = false) LocalDate from,
            @RequestParam(required = false) LocalDate to,
            @RequestParam(required = false) TransactionType type,
            @RequestParam(required = false) String categoryId,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size,
            @RequestParam(defaultValue = "date,desc") String sort) {

        TransactionFilter filter = new TransactionFilter(from, to, type, categoryId, search, page, size, sort);
        return ResponseEntity.ok(PageResponse.from(getTransactionsUseCase.execute(userId, filter)));
    }

    @PostMapping
    public ResponseEntity<TransactionResponse> createTransaction(
            @AuthenticationPrincipal UserId userId,
            @Valid @RequestBody CreateTransactionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(createTransactionUseCase.execute(userId, request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TransactionResponse> getTransaction(
            @AuthenticationPrincipal UserId userId,
            @PathVariable String id) {
        return ResponseEntity.ok(getTransactionUseCase.execute(id, userId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TransactionResponse> updateTransaction(
            @AuthenticationPrincipal UserId userId,
            @PathVariable String id,
            @Valid @RequestBody UpdateTransactionRequest request) {
        return ResponseEntity.ok(updateTransactionUseCase.execute(id, userId, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTransaction(
            @AuthenticationPrincipal UserId userId,
            @PathVariable String id) {
        deleteTransactionUseCase.execute(id, userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/export")
    public ResponseEntity<byte[]> exportTransactions(
            @AuthenticationPrincipal UserId userId,
            @RequestParam(required = false) LocalDate from,
            @RequestParam(required = false) LocalDate to) {
        String csv = exportTransactionsUseCase.execute(userId, from, to);
        byte[] bytes = csv.getBytes(StandardCharsets.UTF_8);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, "text/csv; charset=UTF-8")
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"transactions.csv\"")
                .body(bytes);
    }
}
