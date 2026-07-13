package com.delivery.domain.menu.dto.request;

import jakarta.validation.constraints.NotNull;

public record UpdateMenuVisibilityRequest(
        @NotNull(message = "MENU_HIDDEN_STATUS_REQUIRED") Boolean hidden) {}
