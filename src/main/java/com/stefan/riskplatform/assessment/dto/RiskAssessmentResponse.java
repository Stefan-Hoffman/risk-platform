package com.stefan.riskplatform.assessment.dto;

import com.stefan.riskplatform.common.enums.RiskDecision;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data
@Builder
public class RiskAssessmentResponse {
    private String assessmentId;
    private String eventId;
    private String tenantId;
    private Integer totalScore;
    private RiskDecision decision;
    private Instant evaluatedAt;
    private List<RuleHitResponse> ruleHits;
}