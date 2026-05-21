package com.stefan.riskplatform.behavior.dto;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class EntityBehaviorProfileResponse {

    private String profileId;
    private String tenantId;
    private String entityId;
    private Instant lastSeenAt;
    private String lastIpAddress;
    private String lastDeviceId;
    private Integer totalEvents;
    private Instant createdAt;
    private Instant updatedAt;
}