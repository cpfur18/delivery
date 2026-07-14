package com.delivery.domain.menu.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record UpdateMenuRequest(
        @NotBlank(message = "INVALID_MENU_NAME") @Size(max = 100, message = "INVALID_MENU_NAME")
                String name,
        String description,
        @Positive(message = "INVALID_MENU_PRICE") int price) {}
