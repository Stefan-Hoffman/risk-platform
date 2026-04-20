package com.stefan.riskplatform.rule.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateRiskRuleRequest {

    @NotBlank
    private String name;

    @NotBlank
    private String eventType;

    @NotBlank
    private String conditionsJson;

    @NotNull
    private Integer riskScore;
}