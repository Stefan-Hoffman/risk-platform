package com.stefan.riskplatform.risk.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stefan.riskplatform.rule.entity.RiskRule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class RiskEngineServiceTest {

    private RiskEngineService riskEngineService;

    @BeforeEach
    void setUp() {
        riskEngineService = new RiskEngineService(new ObjectMapper());
    }

    @Test
    void shouldMatchRuleWithAndOperator() {
        RiskRule rule = RiskRule.builder()
                .ruleId("rule_1")
                .eventType("LOGIN")
                .conditionsJson("""
                    {
                      "operator": "AND",
                      "conditions": [
                        {
                          "field": "knownDevice",
                          "operator": "EQUALS",
                          "value": false
                        },
                        {
                          "field": "country",
                          "operator": "NOT_EQUALS",
                          "value": "ZA"
                        }
                      ]
                    }
                    """)
                .riskScore(60)
                .createdAt(Instant.now())
                .build();

        String payloadJson = """
                {
                  "knownDevice": false,
                  "country": "DE"
                }
                """;

        List<RiskRule> matchedRules = riskEngineService.evaluateMatchedRules(List.of(rule), payloadJson);

        assertThat(matchedRules).hasSize(1);
        assertThat(matchedRules.get(0).getRuleId()).isEqualTo("rule_1");
    }

    @Test
    void shouldNotMatchRuleWhenAndConditionFails() {
        RiskRule rule = RiskRule.builder()
                .ruleId("rule_1")
                .eventType("LOGIN")
                .conditionsJson("""
                    {
                      "operator": "AND",
                      "conditions": [
                        {
                          "field": "knownDevice",
                          "operator": "EQUALS",
                          "value": false
                        },
                        {
                          "field": "country",
                          "operator": "NOT_EQUALS",
                          "value": "ZA"
                        }
                      ]
                    }
                    """)
                .riskScore(60)
                .createdAt(Instant.now())
                .build();

        String payloadJson = """
                {
                  "knownDevice": false,
                  "country": "ZA"
                }
                """;

        List<RiskRule> matchedRules = riskEngineService.evaluateMatchedRules(List.of(rule), payloadJson);

        assertThat(matchedRules).isEmpty();
    }

    @Test
    void shouldMatchRuleWithOrOperator() {
        RiskRule rule = RiskRule.builder()
                .ruleId("rule_2")
                .eventType("LOGIN")
                .conditionsJson("""
                    {
                      "operator": "OR",
                      "conditions": [
                        {
                          "field": "vpnDetected",
                          "operator": "EQUALS",
                          "value": true
                        },
                        {
                          "field": "failedAttempts",
                          "operator": "GREATER_THAN_OR_EQUALS",
                          "value": 5
                        }
                      ]
                    }
                    """)
                .riskScore(40)
                .createdAt(Instant.now())
                .build();

        String payloadJson = """
                {
                  "vpnDetected": false,
                  "failedAttempts": 5
                }
                """;

        List<RiskRule> matchedRules = riskEngineService.evaluateMatchedRules(List.of(rule), payloadJson);

        assertThat(matchedRules).hasSize(1);
        assertThat(matchedRules.get(0).getRuleId()).isEqualTo("rule_2");
    }

    @Test
    void shouldSupportGreaterThanComparison() {
        RiskRule rule = RiskRule.builder()
                .ruleId("rule_3")
                .eventType("LOGIN")
                .conditionsJson("""
                    {
                      "operator": "AND",
                      "conditions": [
                        {
                          "field": "failedAttempts",
                          "operator": "GREATER_THAN",
                          "value": 3
                        }
                      ]
                    }
                    """)
                .riskScore(20)
                .createdAt(Instant.now())
                .build();

        String payloadJson = """
                {
                  "failedAttempts": 4
                }
                """;

        int score = riskEngineService.evaluateRisk(List.of(rule), payloadJson);

        assertThat(score).isEqualTo(20);
    }

    @Test
    void shouldSupportLessThanOrEqualsComparison() {
        RiskRule rule = RiskRule.builder()
                .ruleId("rule_4")
                .eventType("LOGIN")
                .conditionsJson("""
                    {
                      "operator": "AND",
                      "conditions": [
                        {
                          "field": "riskScore",
                          "operator": "LESS_THAN_OR_EQUALS",
                          "value": 50
                        }
                      ]
                    }
                    """)
                .riskScore(15)
                .createdAt(Instant.now())
                .build();

        String payloadJson = """
                {
                  "riskScore": 50
                }
                """;

        int score = riskEngineService.evaluateRisk(List.of(rule), payloadJson);

        assertThat(score).isEqualTo(15);
    }

    @Test
    void shouldReturnZeroWhenNoRulesMatch() {
        RiskRule rule = RiskRule.builder()
                .ruleId("rule_5")
                .eventType("LOGIN")
                .conditionsJson("""
                    {
                      "operator": "AND",
                      "conditions": [
                        {
                          "field": "knownDevice",
                          "operator": "EQUALS",
                          "value": false
                        }
                      ]
                    }
                    """)
                .riskScore(60)
                .createdAt(Instant.now())
                .build();

        String payloadJson = """
                {
                  "knownDevice": true
                }
                """;

        int score = riskEngineService.evaluateRisk(List.of(rule), payloadJson);

        assertThat(score).isZero();
    }

    @Test
    void shouldSumScoresOfAllMatchedRules() {
        RiskRule rule1 = RiskRule.builder()
                .ruleId("rule_1")
                .eventType("LOGIN")
                .conditionsJson("""
                    {
                      "operator": "AND",
                      "conditions": [
                        {
                          "field": "knownDevice",
                          "operator": "EQUALS",
                          "value": false
                        }
                      ]
                    }
                    """)
                .riskScore(40)
                .createdAt(Instant.now())
                .build();

        RiskRule rule2 = RiskRule.builder()
                .ruleId("rule_2")
                .eventType("LOGIN")
                .conditionsJson("""
                    {
                      "operator": "AND",
                      "conditions": [
                        {
                          "field": "country",
                          "operator": "NOT_EQUALS",
                          "value": "ZA"
                        }
                      ]
                    }
                    """)
                .riskScore(20)
                .createdAt(Instant.now())
                .build();

        String payloadJson = """
                {
                  "knownDevice": false,
                  "country": "DE"
                }
                """;

        int score = riskEngineService.evaluateRisk(List.of(rule1, rule2), payloadJson);

        assertThat(score).isEqualTo(60);
    }

    @Test
    void shouldReturnEmptyWhenPayloadFieldMissing() {
        RiskRule rule = RiskRule.builder()
                .ruleId("rule_6")
                .eventType("LOGIN")
                .conditionsJson("""
                    {
                      "operator": "AND",
                      "conditions": [
                        {
                          "field": "unknownField",
                          "operator": "EQUALS",
                          "value": true
                        }
                      ]
                    }
                    """)
                .riskScore(50)
                .createdAt(Instant.now())
                .build();

        String payloadJson = """
                {
                  "knownDevice": false
                }
                """;

        List<RiskRule> matchedRules = riskEngineService.evaluateMatchedRules(List.of(rule), payloadJson);

        assertThat(matchedRules).isEmpty();
    }
}