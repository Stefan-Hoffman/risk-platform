package com.stefan.riskplatform.risk.model;

import lombok.Data;

import java.util.List;

@Data
public class RuleConditionGroup {
    private ConditionGroupOperator operator;
    private List<RuleCondition> conditions;
}