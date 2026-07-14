package com.delivery.domain.store.dto.request;

import jakarta.validation.constraints.NotNull;

public record StoreStatusRequest(@NotNull Boolean isOpen) {}