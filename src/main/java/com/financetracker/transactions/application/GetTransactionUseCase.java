package com.financetracker.transactions.application;

import com.financetracker.shared.domain.UserId;
import com.financetracker.shared.domain.exception.NotFoundException;
import com.financetracker.transactions.application.dto.TransactionResponse;
import com.financetracker.transactions.domain.TransactionRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class GetTransactionUseCase {

    private final TransactionRepository transactionRepository;

    public TransactionResponse execute(String id, UserId userId) {
        return transactionRepository.findById(id, userId)
                .map(TransactionResponse::from)
                .orElseThrow(() -> new NotFoundException("Transaction not found"));
    }
}
