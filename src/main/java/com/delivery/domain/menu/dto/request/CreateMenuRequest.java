package com.delivery.domain.menu.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record CreateMenuRequest(
        @NotBlank(message = "INVALID_MENU_NAME") @Size(max = 100, message = "INVALID_MENU_NAME")
                String name,
        String description,
        @Positive(message = "INVALID_MENU_PRICE") int price,
        @NotNull(message = "AI_GENERATION_REQUIRED") Boolean aiGeneration,
        String aiPrompt) {}
