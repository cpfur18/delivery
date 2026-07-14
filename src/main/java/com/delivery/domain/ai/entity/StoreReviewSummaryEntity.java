package com.delivery.domain.ai.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 가게별 AI 리뷰 요약 캐시 - store당 1행. 매일 스케줄러가 새 리뷰가 쌓인 가게만 골라 갱신함
// (reviewCountAtGeneration과 현재 리뷰 개수를 비교해서 동일하면 재생성하지 않음).
@Getter
@Entity
@Table(name = "p_store_review_summary")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StoreReviewSummaryEntity {

    @Id
    @Column(name = "store_id")
    private UUID storeId;

    @Column(name = "summary_text", nullable = false, columnDefinition = "text")
    private String summaryText;

    @Column(name = "review_count_at_generation", nullable = false)
    private long reviewCountAtGeneration;

    @Column(name = "generated_at", nullable = false)
    private LocalDateTime generatedAt;

    private StoreReviewSummaryEntity(
            UUID storeId, String summaryText, long reviewCountAtGeneration) {
        this.storeId = storeId;
        this.summaryText = summaryText;
        this.reviewCountAtGeneration = reviewCountAtGeneration;
        this.generatedAt = LocalDateTime.now();
    }

    public static StoreReviewSummaryEntity create(
            UUID storeId, String summaryText, long reviewCountAtGeneration) {
        return new StoreReviewSummaryEntity(storeId, summaryText, reviewCountAtGeneration);
    }

    public void updateSummary(String summaryText, long reviewCountAtGeneration) {
        this.summaryText = summaryText;
        this.reviewCountAtGeneration = reviewCountAtGeneration;
        this.generatedAt = LocalDateTime.now();
    }
}
