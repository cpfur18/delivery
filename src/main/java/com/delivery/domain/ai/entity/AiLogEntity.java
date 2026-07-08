package com.delivery.domain.ai.entity;

import com.delivery.common.base.BaseCreatedEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(
        name = "p_ai_log",
        indexes = {
            @Index(name = "idx_ai_log_type_created", columnList = "request_type, created_at DESC")
        })
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AiLogEntity extends BaseCreatedEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "ai_log_id")
    private UUID aiLogId;

    @Enumerated(EnumType.STRING)
    @Column(name = "request_type", length = 50, nullable = false)
    private AiRequestType requestType;

    @Column(name = "reference_id")
    private UUID referenceId;

    @Column(name = "request_text", nullable = false, columnDefinition = "text")
    private String requestText;

    @Column(name = "response_text", columnDefinition = "text")
    private String responseText;

    @Column(name = "is_success", nullable = false, columnDefinition = "boolean default true")
    private boolean success;

    public AiLogEntity(
            AiRequestType requestType,
            UUID referenceId,
            String requestText,
            String responseText,
            boolean success) {
        this.requestType = requestType;
        this.referenceId = referenceId;
        this.requestText = requestText;
        this.responseText = responseText;
        this.success = success;
    }
}
