package com.delivery.domain.ai.repository;

import com.delivery.domain.ai.entity.AiLogEntity;
import com.delivery.domain.ai.entity.AiRequestType;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AiLogRepository extends JpaRepository<AiLogEntity, UUID> {

    // 관리자용 AI 로그 조회 - requestType/success 둘 다 선택 조건
    @Query(
            "SELECT a FROM AiLogEntity a WHERE "
                    + "(:requestType IS NULL OR a.requestType = :requestType) "
                    + "AND (:success IS NULL OR a.success = :success)")
    Page<AiLogEntity> search(
            @Param("requestType") AiRequestType requestType,
            @Param("success") Boolean success,
            Pageable pageable);
}
