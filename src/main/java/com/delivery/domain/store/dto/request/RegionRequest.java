package com.delivery.domain.store.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record RegionRequest(
        @NotBlank(message = "REQUIRED_VALUE")
        @Size(min = 1, max = 100, message = "BAD_REQUEST")
        String name,

        @NotNull(message = "REQUIRED_VALUE")
        Double latitude,

        @NotNull(message = "REQUIRED_VALUE")
        Double longitude
) {}