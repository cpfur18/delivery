package com.delivery.domain.menu.dto.request;

import jakarta.validation.constraints.NotNull;

public record ReqCreateMenuDtoV1(
        String name,
        String description,
        int price,
        @NotNull Boolean aiGeneration,
        String aiPrompt) {}
