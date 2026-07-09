package com.delivery.domain.store.service;

import com.delivery.domain.store.dto.CategoryRequestDto;
import com.delivery.domain.store.dto.CategoryResponseDto;
import com.delivery.domain.store.entity.Category;
import com.delivery.domain.store.repository.CategoryRepository;
import com.delivery.global.exception.StoreErrorCode;
import com.delivery.global.exception.StoreException;
import lombok.RequiredArgsConstructor;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    @Transactional
    public CategoryResponseDto createCategory(CategoryRequestDto request) {
        if (categoryRepository.existsByName(request.getName())) {
            throw new StoreException(StoreErrorCode.DUPLICATE_CATEGORY);
        }

        Category category = Category.builder()
                .name(request.getName())
                .build();

        return CategoryResponseDto.from(categoryRepository.save(category));
    }

    @Transactional(readOnly = true)
    public List<CategoryResponseDto> getCategories() {
        return categoryRepository.findAllByDeletedAtIsNull()
                .stream()
                .map(CategoryResponseDto::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public CategoryResponseDto updateCategory(UUID categoryId, CategoryRequestDto request) {
        Category category = categoryRepository.findByCategoryIdAndDeletedAtIsNull(categoryId)
                .orElseThrow(() -> new StoreException(StoreErrorCode.CATEGORY_NOT_FOUND));

        if (categoryRepository.existsByName(request.getName())) {
            throw new StoreException(StoreErrorCode.DUPLICATE_CATEGORY);
        }

        category.update(request.getName());
        return CategoryResponseDto.from(category);
    }

    @Transactional
    public void deleteCategory(UUID categoryId, String deletedBy) {
        Category category = categoryRepository.findByCategoryIdAndDeletedAtIsNull(categoryId)
                .orElseThrow(() -> new StoreException(StoreErrorCode.CATEGORY_NOT_FOUND));

        category.delete(deletedBy);
    }


}