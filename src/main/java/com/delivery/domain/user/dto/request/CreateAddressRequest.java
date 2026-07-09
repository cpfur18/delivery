package com.delivery.domain.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateAddressRequest(
        @NotBlank String address, @NotBlank String addressDetail, @NotNull Boolean isDefault) {}
