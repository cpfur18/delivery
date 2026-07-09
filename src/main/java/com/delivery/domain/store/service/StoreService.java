package com.delivery.domain.store.service;

import com.delivery.domain.review.repository.ReviewRepository;
import com.delivery.domain.store.dto.StoreRequestDto;
import com.delivery.domain.store.dto.StoreResponseDto;
import com.delivery.domain.store.entity.Store;
import com.delivery.domain.store.repository.CategoryRepository;
import com.delivery.domain.store.repository.RegionRepository;
import com.delivery.domain.store.repository.StoreRepository;
import com.delivery.global.exception.StoreErrorCode;
import com.delivery.global.exception.StoreException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StoreService {

    private final StoreRepository storeRepository;
    private final CategoryRepository categoryRepository;
    private final RegionRepository regionRepository;
    private final ReviewRepository reviewRepository;

    // 가게 등록
    @Transactional
    public StoreResponseDto createStore(Long userId, StoreRequestDto request) {

        if (storeRepository.existsByUserIdAndNameAndRegionIdAndDeletedAtIsNull(userId, request.getName(), request.getRegionId())) {
            throw new StoreException(StoreErrorCode.DUPLICATE_STORE);
        }

        categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new StoreException(StoreErrorCode.CATEGORY_NOT_FOUND));

        regionRepository.findById(request.getRegionId())
                .orElseThrow(() -> new StoreException(StoreErrorCode.REGION_NOT_FOUND));

        Store store = Store.builder()
                .userId(userId)
                .categoryId(request.getCategoryId())
                .regionId(request.getRegionId())
                .name(request.getName())
                .address(request.getAddress())
                .phone(request.getPhone())
                .description(request.getDescription())
                .minOrderAmount(request.getMinOrderAmount())
                .isOpen(false)
                .averageRating(0.0)
                .build();

        return StoreResponseDto.from(storeRepository.save(store));
    }

    // 가게 목록 조회
    @Transactional(readOnly = true)
    public Page<StoreResponseDto> getStores(UUID categoryId, UUID regionId, String name, Pageable pageable) {
        return storeRepository.findStores(categoryId, regionId, name, pageable)
                .map(StoreResponseDto::from);
    }

    // 가게 단건 조회
    @Transactional(readOnly = true)
    public StoreResponseDto getStore(UUID storeId) {
        Store store = storeRepository.findByStoreIdAndDeletedAtIsNull(storeId)
                .orElseThrow(() -> new StoreException(StoreErrorCode.STORE_NOT_FOUND));
        return StoreResponseDto.from(store);
    }

    // 가게 수정
    @Transactional
    public StoreResponseDto updateStore(UUID storeId, Long userId, StoreRequestDto request) {
        Store store = storeRepository.findByStoreIdAndDeletedAtIsNull(storeId)
                .orElseThrow(() -> new StoreException(StoreErrorCode.STORE_NOT_FOUND));

        categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new StoreException(StoreErrorCode.CATEGORY_NOT_FOUND));

        regionRepository.findById(request.getRegionId())
                .orElseThrow(() -> new StoreException(StoreErrorCode.REGION_NOT_FOUND));

        store.update(request);
        return StoreResponseDto.from(store);
    }

    // 영업상태 변경
    @Transactional
    public StoreResponseDto updateStoreStatus(UUID storeId, Long userId, Boolean isOpen) {
        Store store = storeRepository.findByStoreIdAndDeletedAtIsNull(storeId)
                .orElseThrow(() -> new StoreException(StoreErrorCode.STORE_NOT_FOUND));

        store.updateStatus(isOpen);
        return StoreResponseDto.from(store);
    }

    // 가게 삭제 (Soft Delete)
    @Transactional
    public void deleteStore(UUID storeId, Long userId) {
        Store store = storeRepository.findByStoreIdAndDeletedAtIsNull(storeId)
                .orElseThrow(() -> new StoreException(StoreErrorCode.STORE_NOT_FOUND));

        store.delete(userId.toString());
    }

    //가게 평점 평균
    @Transactional
    public void updateAverageRating(UUID storeId) {
        Store store = storeRepository.findByStoreIdAndDeletedAtIsNull(storeId)
                .orElseThrow(() -> new StoreException(StoreErrorCode.STORE_NOT_FOUND));

        Double average = reviewRepository.findAverageRatingByStoreId(storeId);
        store.updateAverageRating(average);
    }
}