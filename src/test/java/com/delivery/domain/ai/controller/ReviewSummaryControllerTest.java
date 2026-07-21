package com.delivery.domain.ai.controller;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.delivery.domain.ai.dto.response.ReviewSummaryResponse;
import com.delivery.domain.ai.dto.response.ReviewSummaryStatus;
import com.delivery.domain.ai.entity.StoreReviewSummaryEntity;
import com.delivery.domain.ai.service.ReviewSummaryService;
import com.delivery.domain.store.exception.StoreErrorCode;
import com.delivery.domain.store.exception.StoreException;
import com.delivery.global.exception.ErrorCodeRegistry;
import com.delivery.global.security.jwt.JwtRequestFilter;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

// 이 컨트롤러는 @PreAuthorize가 없는 공개 API라 인증 principal 세팅이 필요 없다
// (SecurityConfig에서 permitAll 처리됨 - MenuControllerTest 등과 달리 인증 우회 설정 불필요).
@Import(ErrorCodeRegistry.class)
@WebMvcTest(ReviewSummaryController.class)
@AutoConfigureMockMvc(addFilters = false)
class ReviewSummaryControllerTest {

    private static final UUID STORE_ID = UUID.randomUUID();

    @Autowired private MockMvc mockMvc;

    @MockitoBean private ReviewSummaryService reviewSummaryService;

    // @WebMvcTest는 Filter 타입 빈을 자동으로 스캔 대상에 포함시키는데,
    // JwtRequestFilter는 JwtUtil에 의존해 실제 컨텍스트 로딩이 실패한다. 모킹으로 우회.
    @MockitoBean private JwtRequestFilter jwtRequestFilter;

    @Nested
    @DisplayName("가게 리뷰 요약 조회")
    class GetReviewSummary {

        @Test
        @DisplayName("리뷰가 10개 미만이면 200과 NOT_ENOUGH_REVIEWS 상태를 반환한다")
        void getReviewSummary_returnsNotEnoughReviews() throws Exception {
            given(reviewSummaryService.getSummary(STORE_ID))
                    .willReturn(ReviewSummaryResponse.notEnoughReviews(3L));

            mockMvc.perform(get("/api/v1/stores/{storeId}/review-summary", STORE_ID))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(
                            jsonPath("$.data.status")
                                    .value(ReviewSummaryStatus.NOT_ENOUGH_REVIEWS.name()))
                    .andExpect(jsonPath("$.data.reviewCount").value(3))
                    .andExpect(jsonPath("$.data.summary").doesNotExist());
        }

        @Test
        @DisplayName("리뷰가 10개 이상인데 아직 생성 전이면 200과 PENDING_GENERATION 상태를 반환한다")
        void getReviewSummary_returnsPendingGeneration() throws Exception {
            given(reviewSummaryService.getSummary(STORE_ID))
                    .willReturn(ReviewSummaryResponse.pendingGeneration(12L));

            mockMvc.perform(get("/api/v1/stores/{storeId}/review-summary", STORE_ID))
                    .andExpect(status().isOk())
                    .andExpect(
                            jsonPath("$.data.status")
                                    .value(ReviewSummaryStatus.PENDING_GENERATION.name()))
                    .andExpect(jsonPath("$.data.reviewCount").value(12));
        }

        @Test
        @DisplayName("요약이 준비되어 있으면 200과 READY 상태 및 요약 내용을 반환한다")
        void getReviewSummary_returnsReady() throws Exception {
            StoreReviewSummaryEntity entity =
                    StoreReviewSummaryEntity.create(STORE_ID, "다들 맛있다고 평가했습니다.", 10L);
            given(reviewSummaryService.getSummary(STORE_ID))
                    .willReturn(ReviewSummaryResponse.ready(entity));

            mockMvc.perform(get("/api/v1/stores/{storeId}/review-summary", STORE_ID))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.status").value(ReviewSummaryStatus.READY.name()))
                    .andExpect(jsonPath("$.data.summary").value("다들 맛있다고 평가했습니다."))
                    .andExpect(jsonPath("$.data.reviewCount").value(10));
        }

        @Test
        @DisplayName("존재하지 않거나 삭제된 가게면 404와 STORE_NOT_FOUND 에러를 반환한다")
        void getReviewSummary_returns404_whenStoreNotFound() throws Exception {
            given(reviewSummaryService.getSummary(eq(STORE_ID)))
                    .willThrow(new StoreException(StoreErrorCode.STORE_NOT_FOUND));

            mockMvc.perform(get("/api/v1/stores/{storeId}/review-summary", STORE_ID))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.error").value(StoreErrorCode.STORE_NOT_FOUND.getName()));
        }
    }
}
