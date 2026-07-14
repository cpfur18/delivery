package com.delivery.domain.review.repository;

import com.delivery.domain.review.entity.Review;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReviewRepository extends JpaRepository<Review, UUID> {

    Optional<Review> findByIdAndDeletedAtIsNull(UUID reviewId);

    List<Review> findAllByDeletedAtIsNull();

    List<Review> findAllByStoreIdAndDeletedAtIsNull(UUID storeId);

    List<Review> findAllByUserIdAndDeletedAtIsNull(Long userId);

    @Query(
            """
            SELECT AVG(r.rating)
            FROM Review r
            WHERE r.storeId = :storeId
              AND r.deletedAt IS NULL
            """)
    Double findAverageRatingByStoreId(@Param("storeId") UUID storeId);

    long countByStoreIdAndDeletedAtIsNull(UUID storeId);

    // AI 리뷰 요약 대상 가게 목록 - 삭제되지 않은 리뷰가 threshold개 이상인 가게 storeId만 반환
    @Query(
            """
            SELECT r.storeId
            FROM Review r
            WHERE r.deletedAt IS NULL
            GROUP BY r.storeId
            HAVING COUNT(r) >= :threshold
            """)
    List<UUID> findStoreIdsWithReviewCountAtLeast(@Param("threshold") long threshold);

    // AI 리뷰 요약 프롬프트 구성용 - 리뷰가 아무리 쌓여도 프롬프트 크기(비용/응답속도)가 고정되도록
    // 최신순 상위 N개만 조회
    List<Review> findTop50ByStoreIdAndDeletedAtIsNullOrderByCreatedAtDesc(UUID storeId);
}
