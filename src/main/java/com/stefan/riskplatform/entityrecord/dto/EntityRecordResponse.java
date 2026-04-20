package com.stefan.riskplatform.entityrecord.dto;

import com.stefan.riskplatform.common.enums.EntityType;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class EntityRecordResponse {
    private String entityId;
    private String tenantId;
    private EntityType entityType;
    private String externalRef;
    private Instant createdAt;
}