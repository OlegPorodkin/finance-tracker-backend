package com.financetracker.transactions.domain;

import com.financetracker.shared.domain.PagedResult;
import com.financetracker.shared.domain.UserId;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TransactionRepository {

    Transaction save(Transaction transaction);

    Optional<Transaction> findById(String id, UserId userId);

    PagedResult<Transaction> findAll(UserId userId, TransactionFilter filter);

    List<Transaction> findAllForExport(UserId userId, LocalDate from, LocalDate to);

    void delete(String id, UserId userId);
}
