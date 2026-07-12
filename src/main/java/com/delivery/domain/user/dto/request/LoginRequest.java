package com.delivery.domain.user.dto.request;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank(message = "REQUIRED_USER_ID") String username,
        @NotBlank(message = "REQUIRED_PASSWORD") String password) {}
