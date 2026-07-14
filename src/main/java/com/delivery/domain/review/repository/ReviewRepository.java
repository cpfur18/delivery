package com.delivery.domain.review.repository;

import com.delivery.domain.review.entity.Review;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.delivery.domain.review.enums.ReviewSortType;
import com.delivery.domain.review.service.ReviewService;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReviewRepository extends JpaRepository<Review, UUID> {

    Optional<Review> findByIdAndDeletedAtIsNull(UUID reviewId);

    List<Review> findAllByDeletedAtIsNull();

    List<Review> findAllByStoreIdAndDeletedAtIsNull(UUID storeId);

    List<Review> findAllByStoreIdAndDeletedAtIsNull(UUID storeId, Sort sort);

    List<Review> findAllByUserIdAndDeletedAtIsNull(Long userId, Sort latestSort);

    boolean existsByOrderId(UUID orderId);

    @Query(
            """
            SELECT AVG(r.rating)
            FROM Review r
            WHERE r.storeId = :storeId
              AND r.deletedAt IS NULL
            """)
    Double findAverageRatingByStoreId(@Param("storeId") UUID storeId);
}
