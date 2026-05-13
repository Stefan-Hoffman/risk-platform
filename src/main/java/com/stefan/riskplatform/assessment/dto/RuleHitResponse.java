package com.stefan.riskplatform.assessment.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RuleHitResponse {
    private Long ruleHitId;
    private String ruleId;
    private Integer riskScore;
}