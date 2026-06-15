package com.financetracker.transactions.application;

import com.financetracker.shared.domain.TransactionPort;
import com.financetracker.shared.domain.UserId;
import com.financetracker.transactions.domain.TransactionRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DeleteTransactionUseCase {

    private final TransactionRepository transactionRepository;
    private final TransactionPort transactionPort;

    public void execute(String id, UserId userId) {
        transactionPort.execute(() -> transactionRepository.delete(id, userId));
    }
}
