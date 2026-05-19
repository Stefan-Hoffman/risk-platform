package com.stefan.riskplatform.rule.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stefan.riskplatform.common.dto.PageResponse;
import com.stefan.riskplatform.common.exception.DuplicateResourceException;
import com.stefan.riskplatform.common.exception.InvalidRuleDefinitionException;
import com.stefan.riskplatform.common.mapper.PageResponseMapper;
import com.stefan.riskplatform.rule.dto.CreateRiskRuleRequest;
import com.stefan.riskplatform.rule.dto.RiskRuleResponse;
import com.stefan.riskplatform.rule.entity.RiskRule;
import com.stefan.riskplatform.rule.mapper.RiskRuleMapper;
import com.stefan.riskplatform.rule.repository.RiskRuleRepository;
import com.stefan.riskplatform.tenant.entity.Tenant;
import com.stefan.riskplatform.tenant.service.TenantService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    private final PageResponseMapper pageResponseMapper;
    private final ObjectMapper objectMapper;

    public RiskRuleResponse createRiskRule(String tenantId, CreateRiskRuleRequest request) {
        Tenant tenant = tenantService.getTenantOrThrow(tenantId);

        if (riskRuleRepository.existsByTenant_TenantIdAndName(tenantId, request.getName())) {
            throw new DuplicateResourceException(
                    "Risk rule already exists for tenant. name=" + request.getName()
            );
        }

        try {
            objectMapper.readTree(request.getConditionsJson());
        } catch (JsonProcessingException ex) {
            throw new InvalidRuleDefinitionException(
                    "Invalid conditionsJson structure"
            );
        }

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
        return riskRuleRepository.findByTenant_TenantIdAndEventTypeAndEnabledTrue(tenantId, eventType);
    }

    public PageResponse<RiskRuleResponse> getRules(
            String tenantId,
            String eventType,
            Boolean enabled,
            Pageable pageable
    ) {
        Page<RiskRule> page;

        if (eventType != null && enabled != null) {
            page = riskRuleRepository.findByTenant_TenantIdAndEventTypeAndEnabled(
                    tenantId, eventType, enabled, pageable
            );
        } else if (eventType != null) {
            page = riskRuleRepository.findByTenant_TenantIdAndEventType(tenantId, eventType, pageable);
        } else if (enabled != null) {
            page = riskRuleRepository.findByTenant_TenantIdAndEnabled(tenantId, enabled, pageable);
        } else {
            page = riskRuleRepository.findByTenant_TenantId(tenantId, pageable);
        }

        return pageResponseMapper.toPageResponse(page.map(riskRuleMapper::toResponse));
    }
}