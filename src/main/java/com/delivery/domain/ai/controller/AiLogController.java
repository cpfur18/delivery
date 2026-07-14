package com.delivery.domain.ai.controller;

import com.delivery.common.RestApiResponse;
import com.delivery.domain.ai.dto.response.AiLogResponse;
import com.delivery.domain.ai.entity.AiRequestType;
import com.delivery.domain.ai.service.AiLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "AI 로그", description = "관리자용 AI 요청/응답 로그 조회 API")
@RestController
@RequiredArgsConstructor
public class AiLogController {

    private final AiLogService aiLogService;

    // size는 10/30/50만 허용(그 외는 10으로 보정), 기본 정렬은 생성일 내림차순
    @Operation(summary = "AI 로그 검색", description = "MANAGER/MASTER가 AI 요청/응답 로그를 조회합니다.")
    @PreAuthorize("hasAnyRole('MANAGER', 'MASTER')")
    @GetMapping("/api/v1/ai-logs")
    public ResponseEntity<RestApiResponse<Page<AiLogResponse>>> searchLogs(
            @RequestParam(required = false) AiRequestType requestType,
            @RequestParam(required = false) Boolean success,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String sort) {
        return ResponseEntity.ok(
                RestApiResponse.success(
                        HttpStatus.OK,
                        "AI 로그 조회에 성공했습니다.",
                        aiLogService.searchLogs(requestType, success, page, size, sort)));
    }
}
