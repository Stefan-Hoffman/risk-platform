package com.stefan.riskplatform.assessment.service;

import com.stefan.riskplatform.assessment.dto.RiskAssessmentResponse;
import com.stefan.riskplatform.assessment.dto.RuleHitResponse;
import com.stefan.riskplatform.assessment.entity.RiskAssessment;
import com.stefan.riskplatform.assessment.entity.RuleHit;
import com.stefan.riskplatform.assessment.mapper.RiskAssessmentMapper;
import com.stefan.riskplatform.assessment.repository.RiskAssessmentRepository;
import com.stefan.riskplatform.assessment.repository.RuleHitRepository;
import com.stefan.riskplatform.common.enums.RiskDecision;
import com.stefan.riskplatform.common.exception.ResourceNotFoundException;
import com.stefan.riskplatform.event.entity.Event;
import com.stefan.riskplatform.rule.entity.RiskRule;
import com.stefan.riskplatform.tenant.entity.Tenant;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RiskAssessmentServiceTest {

    @Mock
    private RiskAssessmentRepository riskAssessmentRepository;

    @Mock
    private RuleHitRepository ruleHitRepository;

    @Mock
    private RiskAssessmentMapper riskAssessmentMapper;

    @InjectMocks
    private RiskAssessmentService riskAssessmentService;

    @Test
    void shouldReturnAssessmentWithRuleHits() {
        Tenant tenant = Tenant.builder()
                .tenantId("tenant_1")
                .build();

        Event event = Event.builder()
                .eventId("event_1")
                .build();

        RiskAssessment assessment = RiskAssessment.builder()
                .assessmentId("assessment_1")
                .event(event)
                .tenant(tenant)
                .totalScore(60)
                .decision(RiskDecision.REVIEW)
                .evaluatedAt(Instant.now())
                .build();

        RiskRule rule = RiskRule.builder()
                .ruleId("rule_1")
                .build();

        RuleHit ruleHit = RuleHit.builder()
                .ruleHitId(1L)
                .assessment(assessment)
                .rule(rule)
                .riskScore(60)
                .build();

        RiskAssessmentResponse assessmentResponse = RiskAssessmentResponse.builder()
                .assessmentId("assessment_1")
                .eventId("event_1")
                .tenantId("tenant_1")
                .totalScore(60)
                .decision(RiskDecision.REVIEW)
                .evaluatedAt(assessment.getEvaluatedAt())
                .build();

        RuleHitResponse ruleHitResponse = RuleHitResponse.builder()
                .ruleHitId(1L)
                .ruleId("rule_1")
                .riskScore(60)
                .build();

        when(riskAssessmentRepository.findById("assessment_1")).thenReturn(Optional.of(assessment));
        when(ruleHitRepository.findByAssessment_AssessmentId("assessment_1")).thenReturn(List.of(ruleHit));
        when(riskAssessmentMapper.toResponse(assessment)).thenReturn(assessmentResponse);
        when(riskAssessmentMapper.toRuleHitResponse(ruleHit)).thenReturn(ruleHitResponse);

        RiskAssessmentResponse result = riskAssessmentService.getAssessmentById("assessment_1");

        assertThat(result.getAssessmentId()).isEqualTo("assessment_1");
        assertThat(result.getRuleHits()).hasSize(1);
        assertThat(result.getRuleHits().get(0).getRuleId()).isEqualTo("rule_1");
    }

    @Test
    void shouldThrowWhenAssessmentNotFound() {
        when(riskAssessmentRepository.findById("missing")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> riskAssessmentService.getAssessmentById("missing"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Assessment not found: missing");
    }
}