package com.stefan.riskplatform.alert.dto;

import com.stefan.riskplatform.common.enums.AlertStatus;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class AlertResponse {
    private String alertId;
    private String tenantId;
    private String entityId;
    private String assessmentId;
    private Integer riskScore;
    private AlertStatus status;
    private Instant createdAt;
}