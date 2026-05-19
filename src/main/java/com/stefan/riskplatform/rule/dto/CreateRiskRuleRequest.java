package com.stefan.riskplatform.rule.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateRiskRuleRequest {

    @NotBlank(message = "name is required")
    private String name;

    @NotBlank(message = "eventType is required")
    private String eventType;

    @NotBlank(message = "conditionsJson is required")
    private String conditionsJson;

    @NotNull(message = "riskScore is required")
    @Min(value = 1, message = "riskScore must be >= 1")
    @Max(value = 100, message = "riskScore must be <= 100")
    private Integer riskScore;
}