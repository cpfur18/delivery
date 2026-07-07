package com.delivery.domain.store.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class CategoryRequestDto {

    @NotBlank
    @Size(min = 1, max = 50)
    private String name;
}