package com.delivery.domain.store.dto.request;

import jakarta.validation.constraints.*;
import java.util.UUID;

public record StoreRequest(
        @NotNull(message = "REQUIRED_VALUE")
        UUID categoryId,

        @NotNull(message = "REQUIRED_VALUE")
        UUID regionId,

        @NotBlank(message = "REQUIRED_VALUE")
        @Size(min = 1, max = 50, message = "BAD_REQUEST")
        String name,

        @NotBlank(message = "REQUIRED_VALUE")
        @Size(max = 255, message = "BAD_REQUEST")
        String address,

        @NotBlank(message = "REQUIRED_VALUE")
        @Size(max = 20, message = "BAD_REQUEST")
        String phone,

        @Size(max = 500, message = "BAD_REQUEST")
        String description,

        @NotNull(message = "REQUIRED_VALUE")
        @Min(value = 0, message = "BAD_REQUEST")
        Integer minOrderAmount
) {}