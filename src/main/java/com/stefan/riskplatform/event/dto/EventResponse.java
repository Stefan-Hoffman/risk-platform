package com.stefan.riskplatform.event.dto;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class EventResponse {
    private String eventId;
    private String tenantId;
    private String entityId;
    private String eventType;
    private Instant eventTimestamp;
    private String source;
    private String ipAddress;
    private String deviceId;
    private String payloadJson;
    private Instant createdAt;
}