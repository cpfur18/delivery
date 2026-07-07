package com.delivery.domain.store.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;

import java.util.UUID;

@Getter
public class StoreRequestDto {

    @NotNull
    private UUID categoryId;

    @NotNull
    private UUID regionId;

    @NotBlank
    @Size(min = 1, max = 50)
    private String name;

    @NotBlank
    @Size(max = 255)
    private String address;

    @NotBlank
    @Size(max = 20)
    private String phone;

    @Size(max = 500)
    private String description;

    @NotNull
    @Min(0)
    private Integer minOrderAmount;
}