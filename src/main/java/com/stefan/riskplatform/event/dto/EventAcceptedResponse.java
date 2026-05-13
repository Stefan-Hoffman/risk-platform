package com.stefan.riskplatform.event.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EventAcceptedResponse {
    private String eventId;
    private String assessmentId;
    private String status;
}