package com.delivery.domain.store.service;

import com.delivery.domain.store.dto.CategoryRequestDto;
import com.delivery.domain.store.dto.CategoryResponseDto;
import com.delivery.domain.store.entity.Category;
import com.delivery.domain.store.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
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
            throw new RuntimeException("이미 등록된 카테고리입니다.");
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
}