package com.stefan.riskplatform.rule.mapper;

import com.stefan.riskplatform.rule.dto.RiskRuleResponse;
import com.stefan.riskplatform.rule.entity.RiskRule;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RiskRuleMapper {

    @Mapping(target = "tenantId", source = "tenant.tenantId")
    RiskRuleResponse toResponse(RiskRule riskRule);
}