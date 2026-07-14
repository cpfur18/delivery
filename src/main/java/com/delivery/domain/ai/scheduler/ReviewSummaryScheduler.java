package com.delivery.domain.ai.scheduler;

import com.delivery.domain.ai.service.ReviewSummaryService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

// 매일 새벽 3시, 리뷰가 10개 이상 쌓인 가게의 AI 요약을 재생성한다.
// 가게 하나가 Gemini 호출 실패해도 나머지 가게 처리는 계속되도록 개별적으로 예외를 잡는다.
@Slf4j
@Component
@RequiredArgsConstructor
public class ReviewSummaryScheduler {

    private final ReviewSummaryService reviewSummaryService;

    @Scheduled(cron = "0 0 3 * * *")
    public void regenerateStoreReviewSummaries() {
        List<UUID> targetStoreIds = reviewSummaryService.findTargetStoreIds();

        for (UUID storeId : targetStoreIds) {
            try {
                reviewSummaryService.regenerateIfStale(storeId);
            } catch (Exception e) {
                log.error("가게 {} 리뷰 요약 갱신 실패", storeId, e);
            }
        }
    }
}
