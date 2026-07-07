package com.delivery.domain.store.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class RegionRequestDto {

    @NotBlank
    @Size(min = 1, max = 100)
    private String name;

    @NotNull
    private Double latitude;

    @NotNull
    private Double longitude;
}