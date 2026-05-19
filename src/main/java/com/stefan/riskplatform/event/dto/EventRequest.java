package com.stefan.riskplatform.event.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Map;

@Data
public class EventRequest {

    @NotBlank(message = "eventType is required")
    private String eventType;

    @NotBlank(message = "entityId is required")
    private String entityId;

    @NotBlank(message = "source is required")
    private String source;

    private String ipAddress;

    private String deviceId;

    @NotNull(message = "payload is required")
    private Map<String, Object> payload;
}