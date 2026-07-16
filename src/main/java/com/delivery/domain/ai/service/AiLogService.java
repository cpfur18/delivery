package com.delivery.domain.ai.service;

import com.delivery.domain.ai.dto.response.AiLogResponse;
import com.delivery.domain.ai.entity.AiLogEntity;
import com.delivery.domain.ai.entity.AiRequestType;
import com.delivery.domain.ai.repository.AiLogRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

// AI 요청/응답 로그는 호출한 쪽(예: 메뉴 등록)의 트랜잭션 성패와 무관하게 항상 남아야 함 -
// REQUIRES_NEW로 별도 트랜잭션에 즉시 커밋해서, 이후 호출자 트랜잭션이 롤백되어도 로그는 유지됨.
@Service
@RequiredArgsConstructor
public class AiLogService {

    private final AiLogRepository aiLogRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveLog(
            AiRequestType requestType,
            UUID referenceId,
            String requestText,
            String responseText,
            boolean success,
            String errorMessage) {
        aiLogRepository.save(
                new AiLogEntity(
                        requestType,
                        referenceId,
                        requestText,
                        responseText,
                        success,
                        errorMessage));
    }

    // 관리자용 AI 로그 조회 - requestType/success 둘 다 선택 조건
    @Transactional(readOnly = true)
    public Page<AiLogResponse> searchLogs(
            AiRequestType requestType, Boolean success, int page, int size, String sort) {
        Pageable pageable = createPageable(page, normalizePageSize(size), sort);
        return aiLogRepository.search(requestType, success, pageable).map(AiLogResponse::from);
    }

    // 요구사항: size는 10/30/50만 허용하고 그 외 값은 에러가 아니라 10으로 보정
    private int normalizePageSize(int size) {
        if (size == 10 || size == 30 || size == 50) {
            return size;
        }
        return 10;
    }

    // 기본 정렬은 생성일 내림차순 - sort=createdAt,asc로 요청한 경우만 오름차순으로 뒤집는다.
    private Pageable createPageable(int page, int size, String sort) {
        Sort.Direction direction =
                "createdAt,asc".equalsIgnoreCase(sort) ? Sort.Direction.ASC : Sort.Direction.DESC;
        return PageRequest.of(Math.max(page, 0), size, Sort.by(direction, "createdAt"));
    }
}
