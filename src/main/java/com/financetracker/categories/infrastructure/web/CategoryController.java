package com.financetracker.categories.infrastructure.web;

import com.financetracker.categories.application.CreateCategoryUseCase;
import com.financetracker.categories.application.GetCategoriesUseCase;
import com.financetracker.categories.application.SoftDeleteCategoryUseCase;
import com.financetracker.categories.application.UpdateCategoryUseCase;
import com.financetracker.categories.application.dto.CategoryResponse;
import com.financetracker.categories.application.dto.CreateCategoryRequest;
import com.financetracker.categories.application.dto.UpdateCategoryRequest;
import com.financetracker.shared.domain.UserId;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final GetCategoriesUseCase getCategoriesUseCase;
    private final CreateCategoryUseCase createCategoryUseCase;
    private final UpdateCategoryUseCase updateCategoryUseCase;
    private final SoftDeleteCategoryUseCase softDeleteCategoryUseCase;

    @GetMapping
    public ResponseEntity<List<CategoryResponse>> getCategories(@AuthenticationPrincipal UserId userId) {
        return ResponseEntity.ok(getCategoriesUseCase.execute(userId));
    }

    @PostMapping
    public ResponseEntity<CategoryResponse> createCategory(@AuthenticationPrincipal UserId userId,
                                                           @Valid @RequestBody CreateCategoryRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(createCategoryUseCase.execute(userId, request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoryResponse> updateCategory(@AuthenticationPrincipal UserId userId,
                                                           @PathVariable String id,
                                                           @Valid @RequestBody UpdateCategoryRequest request) {
        return ResponseEntity.ok(updateCategoryUseCase.execute(id, userId, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@AuthenticationPrincipal UserId userId,
                                               @PathVariable String id) {
        softDeleteCategoryUseCase.execute(id, userId);
        return ResponseEntity.noContent().build();
    }
}
