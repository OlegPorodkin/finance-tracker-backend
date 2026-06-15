package com.financetracker.transactions.infrastructure.persistence;

import com.financetracker.transactions.domain.TransactionFilter;
import com.financetracker.transactions.infrastructure.persistence.entity.TransactionJpaEntity;
import org.springframework.data.jpa.domain.Specification;

import java.util.UUID;

public class TransactionSpecification {

    public static Specification<TransactionJpaEntity> fromFilter(UUID userId, TransactionFilter filter) {
        Specification<TransactionJpaEntity> spec = (root, query, cb) ->
                cb.equal(root.get("userId"), userId);

        if (filter.from() != null) {
            spec = spec.and((root, query, cb) ->
                    cb.greaterThanOrEqualTo(root.get("date"), filter.from()));
        }
        if (filter.to() != null) {
            spec = spec.and((root, query, cb) ->
                    cb.lessThanOrEqualTo(root.get("date"), filter.to()));
        }
        if (filter.type() != null) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("type"), filter.type().name()));
        }
        if (filter.categoryId() != null && !filter.categoryId().isBlank()) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("categoryId"), UUID.fromString(filter.categoryId())));
        }
        if (filter.search() != null && !filter.search().isBlank()) {
            spec = spec.and((root, query, cb) ->
                    cb.like(cb.lower(root.get("description")), "%" + filter.search().toLowerCase() + "%"));
        }
        return spec;
    }
}
