package com.delivery.domain.review.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.delivery.domain.review.entity.Review;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class ReviewRepositoryTest {

    @Autowired private ReviewRepository reviewRepository;

    @Test
    @DisplayName("리뷰 저장")
    void saveReview() {
        // given
        Review review = Review.create(UUID.randomUUID(), 1L, UUID.randomUUID(), 5, "정말 맛있었습니다!");

        // when
        Review savedReview = reviewRepository.save(review);

        // then
        assertThat(savedReview.getId()).isNotNull();
        assertThat(savedReview.getRating()).isEqualTo(5);
        assertThat(savedReview.getContent()).isEqualTo("정말 맛있었습니다!");
    }

    @Test
    @DisplayName("리뷰 단건 조회")
    void findReview() {
        // given
        Review review =
                Review.create(UUID.randomUUID(), 1L, UUID.randomUUID(), 4, "배송도 빠르고 맛있었습니다.");

        Review savedReview = reviewRepository.save(review);

        // when
        Optional<Review> findReview = reviewRepository.findById(savedReview.getId());

        // then
        assertThat(findReview).isPresent();
        assertThat(findReview.get().getId()).isEqualTo(savedReview.getId());
        assertThat(findReview.get().getRating()).isEqualTo(4);
        assertThat(findReview.get().getContent()).isEqualTo("배송도 빠르고 맛있었습니다.");
    }

    @Test
    @DisplayName("가게의 삭제되지 않은 리뷰 개수를 센다")
    void countByStoreIdAndDeletedAtIsNull() {
        // given
        UUID storeId = UUID.randomUUID();
        Review activeReview = Review.create(UUID.randomUUID(), 1L, storeId, 5, "좋아요");
        Review deletedReview = Review.create(UUID.randomUUID(), 2L, storeId, 3, "별로예요");
        deletedReview.delete("2_tester");

        reviewRepository.save(activeReview);
        reviewRepository.save(deletedReview);

        // when
        long count = reviewRepository.countByStoreIdAndDeletedAtIsNull(storeId);

        // then
        assertThat(count).isEqualTo(1);
    }

    @Test
    @DisplayName("리뷰 개수가 threshold 이상인 가게의 storeId만 조회한다")
    void findStoreIdsWithReviewCountAtLeast() {
        // given
        UUID qualifiedStoreId = UUID.randomUUID();
        UUID notQualifiedStoreId = UUID.randomUUID();

        reviewRepository.save(Review.create(UUID.randomUUID(), 1L, qualifiedStoreId, 5, "리뷰1"));
        reviewRepository.save(Review.create(UUID.randomUUID(), 2L, qualifiedStoreId, 4, "리뷰2"));
        reviewRepository.save(Review.create(UUID.randomUUID(), 3L, notQualifiedStoreId, 5, "리뷰1"));

        // when
        List<UUID> storeIds = reviewRepository.findStoreIdsWithReviewCountAtLeast(2);

        // then - 다른 테스트/기존 데이터가 섞인 공유 DB이므로 정확히 이 값만 있다고 단언하지 않고
        // 대상 가게는 포함되고 미달 가게는 제외되는지만 확인한다
        assertThat(storeIds).contains(qualifiedStoreId).doesNotContain(notQualifiedStoreId);
    }
}
