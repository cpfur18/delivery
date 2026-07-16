package com.delivery.domain.ai.dto.response;

public enum ReviewSummaryStatus {
    // 리뷰가 아직 임계치(10개) 미만 - 요약 대상 아님
    NOT_ENOUGH_REVIEWS,
    // 임계치는 넘었지만 스케줄러가 아직 첫 요약을 생성하지 않음
    PENDING_GENERATION,
    // 요약 생성 완료
    READY
}
