package com.stefan.riskplatform.rule.dto;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class RiskRuleResponse {
    private String ruleId;
    private String tenantId;
    private String name;
    private String eventType;
    private String conditionsJson;
    private Integer riskScore;
    private Boolean enabled;
    private Integer version;
    private Instant createdAt;
}