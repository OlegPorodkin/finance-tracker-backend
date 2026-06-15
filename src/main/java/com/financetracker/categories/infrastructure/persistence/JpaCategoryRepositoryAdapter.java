package com.financetracker.categories.infrastructure.persistence;

import com.financetracker.categories.domain.Category;
import com.financetracker.categories.domain.CategoryRepository;
import com.financetracker.categories.infrastructure.persistence.entity.CategoryJpaEntity;
import com.financetracker.shared.domain.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class JpaCategoryRepositoryAdapter implements CategoryRepository {

    private final SpringDataCategoryRepository springDataCategoryRepository;

    @Override
    public List<Category> findAllByUserId(UserId userId) {
        return springDataCategoryRepository
                .findAllByUserIdOrDefault(userId.value())
                .stream()
                .map(CategoryJpaEntity::toCategory)
                .toList();
    }

    @Override
    public Optional<Category> findById(String id, UserId userId) {
        return springDataCategoryRepository
                .findByIdAndUserIdOrDefault(UUID.fromString(id), userId.value())
                .map(CategoryJpaEntity::toCategory);
    }

    @Override
    public Category save(Category category) {
        CategoryJpaEntity entity = CategoryJpaEntity.fromCategory(category);
        return springDataCategoryRepository.save(entity).toCategory();
    }
}
