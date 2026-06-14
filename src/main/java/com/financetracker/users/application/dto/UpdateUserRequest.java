package com.financetracker.users.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateUserRequest(
        @NotBlank @Size(max = 255) String name,
        @NotBlank @Size(max = 10) String currency
) {}
