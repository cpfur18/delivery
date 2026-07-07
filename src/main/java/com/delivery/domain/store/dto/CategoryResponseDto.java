package com.delivery.domain.store.dto;

import com.delivery.domain.store.entity.Category;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
public class CategoryResponseDto {

    private UUID categoryId;
    private String name;

    public static CategoryResponseDto from(Category category) {
        return CategoryResponseDto.builder()
                .categoryId(category.getCategoryId())
                .name(category.getName())
                .build();
    }
}