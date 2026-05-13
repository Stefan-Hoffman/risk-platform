package com.stefan.riskplatform.risk.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stefan.riskplatform.risk.model.ConditionGroupOperator;
import com.stefan.riskplatform.risk.model.ConditionOperator;
import com.stefan.riskplatform.risk.model.RuleCondition;
import com.stefan.riskplatform.risk.model.RuleConditionGroup;
import com.stefan.riskplatform.rule.entity.RiskRule;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RiskEngineService {

    private final ObjectMapper objectMapper;

    public int evaluateRisk(List<RiskRule> rules, String payloadJson) {
        return evaluateMatchedRules(rules, payloadJson)
                .stream()
                .mapToInt(RiskRule::getRiskScore)
                .sum();
    }

    public List<RiskRule> evaluateMatchedRules(List<RiskRule> rules, String payloadJson) {
        List<RiskRule> matchedRules = new ArrayList<>();

        try {
            JsonNode payload = objectMapper.readTree(payloadJson);

            for (RiskRule rule : rules) {
                if (matches(rule, payload)) {
                    matchedRules.add(rule);
                }
            }

            return matchedRules;
        } catch (Exception e) {
            throw new RuntimeException("Failed to evaluate risk", e);
        }
    }

    private boolean matches(RiskRule rule, JsonNode payload) {
        try {
            RuleConditionGroup group =
                    objectMapper.readValue(rule.getConditionsJson(), RuleConditionGroup.class);

            if (group.getConditions() == null || group.getConditions().isEmpty()) {
                return false;
            }

            ConditionGroupOperator groupOperator =
                    group.getOperator() == null ? ConditionGroupOperator.AND : group.getOperator();

            return switch (groupOperator) {
                case AND -> group.getConditions().stream().allMatch(condition -> matchesCondition(condition, payload));
                case OR -> group.getConditions().stream().anyMatch(condition -> matchesCondition(condition, payload));
            };

        } catch (Exception e) {
            return false;
        }
    }

    private boolean matchesCondition(RuleCondition condition, JsonNode payload) {
        JsonNode actualNode = payload.get(condition.getField());

        if (actualNode == null || actualNode.isNull()) {
            return false;
        }

        ConditionOperator operator = condition.getOperator();
        if (operator == null) {
            operator = ConditionOperator.EQUALS;
        }

        Object expectedValue = condition.getValue();

        return switch (operator) {
            case EQUALS -> equalsValue(actualNode, expectedValue);
            case NOT_EQUALS -> !equalsValue(actualNode, expectedValue);
            case GREATER_THAN -> compare(actualNode, expectedValue) > 0;
            case GREATER_THAN_OR_EQUALS -> compare(actualNode, expectedValue) >= 0;
            case LESS_THAN -> compare(actualNode, expectedValue) < 0;
            case LESS_THAN_OR_EQUALS -> compare(actualNode, expectedValue) <= 0;
        };
    }

    private boolean equalsValue(JsonNode actualNode, Object expectedValue) {
        if (actualNode.isBoolean() && expectedValue instanceof Boolean bool) {
            return actualNode.booleanValue() == bool;
        }

        if (actualNode.isNumber() && expectedValue instanceof Number number) {
            return new BigDecimal(actualNode.asText())
                    .compareTo(new BigDecimal(number.toString())) == 0;
        }

        return actualNode.asText().equals(String.valueOf(expectedValue));
    }

    private int compare(JsonNode actualNode, Object expectedValue) {
        if (!actualNode.isNumber() || !(expectedValue instanceof Number number)) {
            return actualNode.asText().compareTo(String.valueOf(expectedValue));
        }

        BigDecimal actual = new BigDecimal(actualNode.asText());
        BigDecimal expected = new BigDecimal(number.toString());
        return actual.compareTo(expected);
    }
}