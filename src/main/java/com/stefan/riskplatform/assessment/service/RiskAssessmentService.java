package com.stefan.riskplatform.assessment.service;

import com.stefan.riskplatform.assessment.dto.RiskAssessmentResponse;
import com.stefan.riskplatform.assessment.entity.RiskAssessment;
import com.stefan.riskplatform.assessment.entity.RuleHit;
import com.stefan.riskplatform.assessment.mapper.RiskAssessmentMapper;
import com.stefan.riskplatform.assessment.repository.RiskAssessmentRepository;
import com.stefan.riskplatform.assessment.repository.RuleHitRepository;
import com.stefan.riskplatform.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RiskAssessmentService {

    private final RiskAssessmentRepository riskAssessmentRepository;
    private final RuleHitRepository ruleHitRepository;
    private final RiskAssessmentMapper riskAssessmentMapper;

    public RiskAssessmentResponse getAssessmentById(String assessmentId) {
        RiskAssessment assessment = riskAssessmentRepository.findById(assessmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Assessment not found: " + assessmentId));

        List<RuleHit> ruleHits = ruleHitRepository.findByAssessment_AssessmentId(assessmentId);

        RiskAssessmentResponse response = riskAssessmentMapper.toResponse(assessment);
        response.setRuleHits(
                ruleHits.stream()
                        .map(riskAssessmentMapper::toRuleHitResponse)
                        .toList()
        );

        return response;
    }
}