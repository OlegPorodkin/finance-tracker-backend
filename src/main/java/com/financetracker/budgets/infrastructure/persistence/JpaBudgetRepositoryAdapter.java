package com.financetracker.budgets.infrastructure.persistence;

import com.financetracker.budgets.domain.Budget;
import com.financetracker.budgets.domain.BudgetPeriod;
import com.financetracker.budgets.domain.BudgetRepository;
import com.financetracker.budgets.infrastructure.persistence.entity.BudgetJpaEntity;
import com.financetracker.shared.domain.UserId;
import com.financetracker.shared.domain.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class JpaBudgetRepositoryAdapter implements BudgetRepository {

    private final SpringDataBudgetRepository springDataBudgetRepository;

    @Override
    public List<Budget> findAllByUserId(UserId userId) {
        return springDataBudgetRepository.findAllByUserId(userId.value())
                .stream()
                .map(BudgetJpaEntity::toBudget)
                .toList();
    }

    @Override
    public Optional<Budget> findById(String id, UserId userId) {
        return springDataBudgetRepository
                .findByIdAndUserId(UUID.fromString(id), userId.value())
                .map(BudgetJpaEntity::toBudget);
    }

    @Override
    public boolean existsByUserIdAndCategoryIdAndPeriod(UserId userId, String categoryId, BudgetPeriod period) {
        return springDataBudgetRepository.existsByUserIdAndCategoryIdAndPeriod(
                userId.value(), UUID.fromString(categoryId), period.name());
    }

    @Override
    public Budget save(Budget budget) {
        return springDataBudgetRepository.save(BudgetJpaEntity.fromBudget(budget)).toBudget();
    }

    @Override
    public void delete(String id, UserId userId) {
        BudgetJpaEntity entity = springDataBudgetRepository
                .findByIdAndUserId(UUID.fromString(id), userId.value())
                .orElseThrow(() -> new NotFoundException("Budget not found"));
        springDataBudgetRepository.delete(entity);
    }

    @Override
    public long sumSpentInCents(UserId userId, String categoryId, LocalDate from, LocalDate to) {
        return springDataBudgetRepository.sumSpentInCents(
                userId.value(), UUID.fromString(categoryId), from, to);
    }
}
