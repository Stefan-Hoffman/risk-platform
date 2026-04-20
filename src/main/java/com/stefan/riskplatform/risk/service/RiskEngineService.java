package com.stefan.riskplatform.risk.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stefan.riskplatform.rule.entity.RiskRule;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RiskEngineService {

    private final ObjectMapper objectMapper;

    public int evaluateRisk(List<RiskRule> rules, String payloadJson) {
        int totalScore = 0;

        try {
            JsonNode payload = objectMapper.readTree(payloadJson);

            for (RiskRule rule : rules) {
                if (matches(rule, payload)) {
                    totalScore += rule.getRiskScore();
                }
            }

        } catch (Exception e) {
            throw new RuntimeException("Failed to evaluate risk", e);
        }

        return totalScore;
    }

    private boolean matches(RiskRule rule, JsonNode payload) {
        try {
            JsonNode condition = objectMapper.readTree(rule.getConditionsJson());

            String field = condition.get("field").asText();
            JsonNode expectedValue = condition.get("value");

            JsonNode actualValue = payload.get(field);

            if (actualValue == null) {
                return false;
            }

            return actualValue.equals(expectedValue);

        } catch (Exception e) {
            return false;
        }
    }
}