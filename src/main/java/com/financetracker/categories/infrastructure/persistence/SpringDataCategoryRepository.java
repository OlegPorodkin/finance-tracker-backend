package com.financetracker.categories.infrastructure.persistence;

import com.financetracker.categories.infrastructure.persistence.entity.CategoryJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SpringDataCategoryRepository extends JpaRepository<CategoryJpaEntity, UUID> {

    @Query("SELECT c FROM CategoryJpaEntity c WHERE c.userId = :userId OR c.userId IS NULL")
    List<CategoryJpaEntity> findAllByUserIdOrDefault(@Param("userId") UUID userId);

    @Query("SELECT c FROM CategoryJpaEntity c WHERE c.id = :id AND (c.userId = :userId OR c.userId IS NULL)")
    Optional<CategoryJpaEntity> findByIdAndUserIdOrDefault(@Param("id") UUID id, @Param("userId") UUID userId);
}
