package com.financetracker.transactions.application;

import com.financetracker.shared.domain.PagedResult;
import com.financetracker.shared.domain.UserId;
import com.financetracker.transactions.application.dto.TransactionResponse;
import com.financetracker.transactions.domain.TransactionFilter;
import com.financetracker.transactions.domain.TransactionRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class GetTransactionsUseCase {

    private final TransactionRepository transactionRepository;

    public PagedResult<TransactionResponse> execute(UserId userId, TransactionFilter filter) {
        PagedResult<com.financetracker.transactions.domain.Transaction> result =
                transactionRepository.findAll(userId, filter);
        return new PagedResult<>(
                result.content().stream().map(TransactionResponse::from).toList(),
                result.page(),
                result.size(),
                result.totalElements(),
                result.totalPages()
        );
    }
}
