package com.delivery.domain.ai.repository;

import com.delivery.domain.ai.entity.StoreReviewSummaryEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StoreReviewSummaryRepository
        extends JpaRepository<StoreReviewSummaryEntity, UUID> {}
