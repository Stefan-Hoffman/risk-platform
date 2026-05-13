package com.stefan.riskplatform.risk.model;

import lombok.Data;

@Data
public class RuleCondition {
    private String field;
    private ConditionOperator operator;
    private Object value;
}