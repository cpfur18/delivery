package com.delivery.domain.ai.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.delivery.domain.ai.dto.response.AiLogResponse;
import com.delivery.domain.ai.entity.AiLogEntity;
import com.delivery.domain.ai.entity.AiRequestType;
import com.delivery.domain.ai.repository.AiLogRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@ExtendWith(MockitoExtension.class)
class AiLogServiceTest {

    @Mock private AiLogRepository aiLogRepository;

    @InjectMocks private AiLogService aiLogService;

    @Nested
    @DisplayName("AI 로그 검색")
    class SearchLogs {

        @Test
        @DisplayName("검색 결과를 응답 DTO로 변환해 반환한다")
        void searchLogs_returnsMappedResponses() {

            AiLogEntity log =
                    new AiLogEntity(
                            AiRequestType.PRODUCT_DESCRIPTION,
                            null,
                            "설명 생성 프롬프트",
                            "생성된 설명",
                            true,
                            null);

            given(aiLogRepository.search(eq(AiRequestType.PRODUCT_DESCRIPTION), eq(true), any()))
                    .willReturn(new PageImpl<>(java.util.List.of(log)));

            Page<AiLogResponse> result =
                    aiLogService.searchLogs(AiRequestType.PRODUCT_DESCRIPTION, true, 0, 10, null);

            assertThat(result.getContent()).containsExactly(AiLogResponse.from(log));
        }

        @Test
        @DisplayName("size가 10/30/50이 아니면 10으로 보정한다")
        void searchLogs_normalizesInvalidSizeTo10() {

            given(aiLogRepository.search(any(), any(), any()))
                    .willReturn(new PageImpl<>(java.util.List.of()));

            aiLogService.searchLogs(null, null, 0, 999, null);

            ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);
            verify(aiLogRepository).search(any(), any(), captor.capture());
            assertThat(captor.getValue().getPageSize()).isEqualTo(10);
        }

        @Test
        @DisplayName("기본 정렬은 생성일 내림차순이고, sort=createdAt,asc면 오름차순으로 뒤집는다")
        void searchLogs_sortsByCreatedAt() {

            given(aiLogRepository.search(any(), any(), any()))
                    .willReturn(new PageImpl<>(java.util.List.of()));

            aiLogService.searchLogs(null, null, 0, 10, "createdAt,asc");

            ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);
            verify(aiLogRepository).search(any(), any(), captor.capture());
            assertThat(captor.getValue().getSort().getOrderFor("createdAt").getDirection())
                    .isEqualTo(Sort.Direction.ASC);
        }
    }
}
