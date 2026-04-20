package com.stefan.riskplatform.event.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Map;

@Data
public class EventRequest {

    @NotBlank
    private String eventType;

    @NotBlank
    private String entityId;

    @NotBlank
    private String source;

    private String ipAddress;

    private String deviceId;

    @NotNull
    private Map<String, Object> payload;
}