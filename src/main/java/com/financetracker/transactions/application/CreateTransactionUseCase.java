package com.financetracker.transactions.application;

import com.financetracker.shared.domain.Money;
import com.financetracker.shared.domain.TransactionPort;
import com.financetracker.shared.domain.UserId;
import com.financetracker.shared.domain.exception.NotFoundException;
import com.financetracker.transactions.application.dto.CreateTransactionRequest;
import com.financetracker.transactions.application.dto.TransactionResponse;
import com.financetracker.transactions.domain.Transaction;
import com.financetracker.transactions.domain.TransactionRepository;
import com.financetracker.users.domain.UserProfileRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CreateTransactionUseCase {

    private final TransactionRepository transactionRepository;
    private final UserProfileRepository userProfileRepository;
    private final TransactionPort transactionPort;

    public TransactionResponse execute(UserId userId, CreateTransactionRequest request) {
        return transactionPort.execute(() -> {
            String currency = userProfileRepository.findById(userId)
                    .orElseThrow(() -> new NotFoundException("User not found"))
                    .getCurrency();
            Transaction transaction = Transaction.create(
                    userId,
                    request.type(),
                    new Money(request.amountInCents(), currency),
                    request.date(),
                    request.description(),
                    request.categoryId(),
                    request.notes()
            );
            return TransactionResponse.from(transactionRepository.save(transaction));
        });
    }
}
