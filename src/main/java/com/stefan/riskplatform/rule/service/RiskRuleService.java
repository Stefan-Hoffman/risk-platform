package com.stefan.riskplatform.rule.service;

import com.stefan.riskplatform.rule.dto.CreateRiskRuleRequest;
import com.stefan.riskplatform.rule.dto.RiskRuleResponse;
import com.stefan.riskplatform.rule.entity.RiskRule;
import com.stefan.riskplatform.rule.mapper.RiskRuleMapper;
import com.stefan.riskplatform.rule.repository.RiskRuleRepository;
import com.stefan.riskplatform.tenant.entity.Tenant;
import com.stefan.riskplatform.tenant.service.TenantService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RiskRuleService {

    private final RiskRuleRepository riskRuleRepository;
    private final RiskRuleMapper riskRuleMapper;
    private final TenantService tenantService;

    public RiskRuleResponse createRiskRule(String tenantId, CreateRiskRuleRequest request) {
        Tenant tenant = tenantService.getTenantOrThrow(tenantId);

        RiskRule riskRule = RiskRule.builder()
                .ruleId(UUID.randomUUID().toString())
                .tenant(tenant)
                .name(request.getName())
                .eventType(request.getEventType())
                .conditionsJson(request.getConditionsJson())
                .riskScore(request.getRiskScore())
                .enabled(true)
                .version(1)
                .createdAt(Instant.now())
                .build();

        RiskRule savedRule = riskRuleRepository.save(riskRule);
        return riskRuleMapper.toResponse(savedRule);
    }

    public List<RiskRuleResponse> getEnabledRulesByTenantAndEventType(String tenantId, String eventType) {
        return riskRuleRepository.findByTenant_TenantIdAndEventTypeAndEnabledTrue(tenantId, eventType)
                .stream()
                .map(riskRuleMapper::toResponse)
                .toList();
    }

    public List<RiskRule> getEnabledRuleEntities(String tenantId, String eventType) {
        return riskRuleRepository.findByTenant_TenantIdAndEventTypeAndEnabledTrue(
                tenantId,
                eventType
        );
    }
}