package com.stefan.riskplatform.assessment.mapper;

import com.stefan.riskplatform.assessment.dto.RiskAssessmentResponse;
import com.stefan.riskplatform.assessment.dto.RuleHitResponse;
import com.stefan.riskplatform.assessment.entity.RiskAssessment;
import com.stefan.riskplatform.assessment.entity.RuleHit;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RiskAssessmentMapper {

    @Mapping(target = "eventId", source = "event.eventId")
    @Mapping(target = "tenantId", source = "tenant.tenantId")
    @Mapping(target = "ruleHits", ignore = true)
    RiskAssessmentResponse toResponse(RiskAssessment assessment);

    @Mapping(target = "ruleId", source = "rule.ruleId")
    RuleHitResponse toRuleHitResponse(RuleHit ruleHit);
}