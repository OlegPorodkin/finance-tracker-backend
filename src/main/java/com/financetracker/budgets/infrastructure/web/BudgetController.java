package com.financetracker.budgets.infrastructure.web;

import com.financetracker.budgets.application.CreateBudgetUseCase;
import com.financetracker.budgets.application.DeleteBudgetUseCase;
import com.financetracker.budgets.application.GetBudgetStatusUseCase;
import com.financetracker.budgets.application.GetBudgetsUseCase;
import com.financetracker.budgets.application.UpdateBudgetUseCase;
import com.financetracker.budgets.application.dto.BudgetResponse;
import com.financetracker.budgets.application.dto.BudgetStatusResponse;
import com.financetracker.budgets.application.dto.CreateBudgetRequest;
import com.financetracker.budgets.application.dto.UpdateBudgetRequest;
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
@RequestMapping("/api/v1/budgets")
@RequiredArgsConstructor
public class BudgetController {

    private final GetBudgetsUseCase getBudgetsUseCase;
    private final GetBudgetStatusUseCase getBudgetStatusUseCase;
    private final CreateBudgetUseCase createBudgetUseCase;
    private final UpdateBudgetUseCase updateBudgetUseCase;
    private final DeleteBudgetUseCase deleteBudgetUseCase;

    @GetMapping
    public ResponseEntity<List<BudgetResponse>> getBudgets(@AuthenticationPrincipal UserId userId) {
        return ResponseEntity.ok(getBudgetsUseCase.execute(userId));
    }

    @GetMapping("/status")
    public ResponseEntity<List<BudgetStatusResponse>> getBudgetStatus(@AuthenticationPrincipal UserId userId) {
        return ResponseEntity.ok(getBudgetStatusUseCase.execute(userId));
    }

    @PostMapping
    public ResponseEntity<BudgetResponse> createBudget(@AuthenticationPrincipal UserId userId,
                                                        @Valid @RequestBody CreateBudgetRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(createBudgetUseCase.execute(userId, request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BudgetResponse> updateBudget(@AuthenticationPrincipal UserId userId,
                                                        @PathVariable String id,
                                                        @Valid @RequestBody UpdateBudgetRequest request) {
        return ResponseEntity.ok(updateBudgetUseCase.execute(id, userId, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBudget(@AuthenticationPrincipal UserId userId,
                                              @PathVariable String id) {
        deleteBudgetUseCase.execute(id, userId);
        return ResponseEntity.noContent().build();
    }
}
