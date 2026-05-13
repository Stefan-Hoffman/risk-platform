package com.stefan.riskplatform.assessment.controller;

import com.stefan.riskplatform.assessment.dto.RiskAssessmentResponse;
import com.stefan.riskplatform.assessment.dto.RuleHitResponse;
import com.stefan.riskplatform.assessment.service.RiskAssessmentService;
import com.stefan.riskplatform.common.enums.RiskDecision;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RiskAssessmentController.class)
class RiskAssessmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RiskAssessmentService riskAssessmentService;

    @Test
    void shouldGetAssessmentById() throws Exception {
        RiskAssessmentResponse response = RiskAssessmentResponse.builder()
                .assessmentId("assessment_1")
                .eventId("event_1")
                .tenantId("tenant_1")
                .totalScore(60)
                .decision(RiskDecision.REVIEW)
                .evaluatedAt(Instant.now())
                .ruleHits(List.of(
                        RuleHitResponse.builder()
                                .ruleHitId(1L)
                                .ruleId("rule_1")
                                .riskScore(60)
                                .build()
                ))
                .build();

        when(riskAssessmentService.getAssessmentById("assessment_1")).thenReturn(response);

        mockMvc.perform(get("/api/v1/assessments/assessment_1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.assessmentId").value("assessment_1"))
                .andExpect(jsonPath("$.eventId").value("event_1"))
                .andExpect(jsonPath("$.ruleHits[0].ruleId").value("rule_1"));
    }
}