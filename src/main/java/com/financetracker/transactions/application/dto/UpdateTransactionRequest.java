package com.financetracker.transactions.application.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record UpdateTransactionRequest(
        @Min(1) long amountInCents,
        @NotNull LocalDate date,
        @NotBlank @Size(max = 500) String description,
        String categoryId,
        @Size(max = 1000) String notes
) {}
