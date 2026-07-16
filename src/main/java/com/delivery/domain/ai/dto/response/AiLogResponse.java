package com.delivery.domain.ai.dto.response;

import com.delivery.domain.ai.entity.AiLogEntity;
import com.delivery.domain.ai.entity.AiRequestType;
import java.time.LocalDateTime;
import java.util.UUID;

// 관리자(MANAGER/MASTER) 전용 조회 응답 - 별도 공개 필드 분기 없이 전부 노출.
public record AiLogResponse(
        UUID aiLogId,
        AiRequestType requestType,
        UUID referenceId,
        String requestText,
        String responseText,
        boolean success,
        String errorMessage,
        LocalDateTime createdAt,
        String createdBy) {

    public static AiLogResponse from(AiLogEntity log) {
        return new AiLogResponse(
                log.getAiLogId(),
                log.getRequestType(),
                log.getReferenceId(),
                log.getRequestText(),
                log.getResponseText(),
                log.isSuccess(),
                log.getErrorMessage(),
                log.getCreatedAt(),
                log.getCreatedBy());
    }
}
