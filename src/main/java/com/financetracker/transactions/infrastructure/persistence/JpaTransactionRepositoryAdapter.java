package com.financetracker.transactions.infrastructure.persistence;

import com.financetracker.shared.domain.PagedResult;
import com.financetracker.shared.domain.UserId;
import com.financetracker.shared.domain.exception.NotFoundException;
import com.financetracker.transactions.domain.Transaction;
import com.financetracker.transactions.domain.TransactionFilter;
import com.financetracker.transactions.domain.TransactionRepository;
import com.financetracker.transactions.infrastructure.persistence.entity.TransactionJpaEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class JpaTransactionRepositoryAdapter implements TransactionRepository {

    private final SpringDataTransactionRepository springDataTransactionRepository;

    @Override
    public Transaction save(Transaction transaction) {
        return springDataTransactionRepository.save(TransactionJpaEntity.fromTransaction(transaction)).toTransaction();
    }

    @Override
    public Optional<Transaction> findById(String id, UserId userId) {
        return springDataTransactionRepository
                .findByIdAndUserId(UUID.fromString(id), userId.value())
                .map(TransactionJpaEntity::toTransaction);
    }

    @Override
    public PagedResult<Transaction> findAll(UserId userId, TransactionFilter filter) {
        Pageable pageable = PageRequest.of(filter.page(), filter.size(), parseSort(filter.sort()));
        var spec = TransactionSpecification.fromFilter(userId.value(), filter);
        Page<TransactionJpaEntity> page = springDataTransactionRepository.findAll(spec, pageable);
        List<Transaction> content = page.getContent().stream()
                .map(TransactionJpaEntity::toTransaction)
                .toList();
        return new PagedResult<>(content, page.getNumber(), page.getSize(),
                page.getTotalElements(), page.getTotalPages());
    }

    @Override
    public List<Transaction> findAllForExport(UserId userId, LocalDate from, LocalDate to) {
        return springDataTransactionRepository
                .findAllForExport(userId.value(), from, to)
                .stream()
                .map(TransactionJpaEntity::toTransaction)
                .toList();
    }

    @Override
    public void delete(String id, UserId userId) {
        TransactionJpaEntity entity = springDataTransactionRepository
                .findByIdAndUserId(UUID.fromString(id), userId.value())
                .orElseThrow(() -> new NotFoundException("Transaction not found"));
        springDataTransactionRepository.delete(entity);
    }

    private Sort parseSort(String sort) {
        if (sort == null || sort.isBlank()) return Sort.by(Sort.Direction.DESC, "date");
        String[] parts = sort.split(",");
        String field = parts[0].trim();
        Sort.Direction direction = parts.length > 1 && "asc".equalsIgnoreCase(parts[1].trim())
                ? Sort.Direction.ASC : Sort.Direction.DESC;
        return Sort.by(direction, field);
    }
}
