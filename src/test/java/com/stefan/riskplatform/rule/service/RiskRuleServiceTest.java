package com.stefan.riskplatform.rule.service;

import com.stefan.riskplatform.common.enums.TenantStatus;
import com.stefan.riskplatform.rule.dto.CreateRiskRuleRequest;
import com.stefan.riskplatform.rule.dto.RiskRuleResponse;
import com.stefan.riskplatform.rule.entity.RiskRule;
import com.stefan.riskplatform.rule.mapper.RiskRuleMapper;
import com.stefan.riskplatform.rule.repository.RiskRuleRepository;
import com.stefan.riskplatform.tenant.entity.Tenant;
import com.stefan.riskplatform.tenant.service.TenantService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.stefan.riskplatform.common.dto.PageResponse;
import com.stefan.riskplatform.common.mapper.PageResponseMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RiskRuleServiceTest {

    @Mock
    private RiskRuleRepository riskRuleRepository;

    @Mock
    private RiskRuleMapper riskRuleMapper;

    @Mock
    private TenantService tenantService;

    @InjectMocks
    private RiskRuleService riskRuleService;

    @Mock
    private PageResponseMapper pageResponseMapper;

    @Test
    void shouldCreateRiskRule() {
        Tenant tenant = Tenant.builder()
                .tenantId("tenant_1")
                .status(TenantStatus.ACTIVE)
                .createdAt(Instant.now())
                .build();

        CreateRiskRuleRequest request = new CreateRiskRuleRequest();
        request.setName("New Device Login");
        request.setEventType("LOGIN");
        request.setConditionsJson("{\"field\":\"knownDevice\",\"value\":false}");
        request.setRiskScore(40);

        RiskRule saved = RiskRule.builder()
                .ruleId("rule_1")
                .tenant(tenant)
                .name("New Device Login")
                .eventType("LOGIN")
                .conditionsJson(request.getConditionsJson())
                .riskScore(40)
                .enabled(true)
                .version(1)
                .createdAt(Instant.now())
                .build();

        RiskRuleResponse response = RiskRuleResponse.builder()
                .ruleId("rule_1")
                .tenantId("tenant_1")
                .name("New Device Login")
                .eventType("LOGIN")
                .conditionsJson(request.getConditionsJson())
                .riskScore(40)
                .enabled(true)
                .version(1)
                .createdAt(saved.getCreatedAt())
                .build();

        when(tenantService.getTenantOrThrow("tenant_1")).thenReturn(tenant);
        when(riskRuleRepository.save(any(RiskRule.class))).thenReturn(saved);
        when(riskRuleMapper.toResponse(saved)).thenReturn(response);

        RiskRuleResponse result = riskRuleService.createRiskRule("tenant_1", request);

        assertThat(result.getRuleId()).isEqualTo("rule_1");
        assertThat(result.getEventType()).isEqualTo("LOGIN");
    }

    @Test
    void shouldReturnEnabledRulesByTenantAndEventType() {
        RiskRule rule = RiskRule.builder()
                .ruleId("rule_1")
                .eventType("LOGIN")
                .build();

        RiskRuleResponse response = RiskRuleResponse.builder()
                .ruleId("rule_1")
                .eventType("LOGIN")
                .build();

        when(riskRuleRepository.findByTenant_TenantIdAndEventTypeAndEnabledTrue("tenant_1", "LOGIN"))
                .thenReturn(List.of(rule));
        when(riskRuleMapper.toResponse(rule)).thenReturn(response);

        List<RiskRuleResponse> result =
                riskRuleService.getEnabledRulesByTenantAndEventType("tenant_1", "LOGIN");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getRuleId()).isEqualTo("rule_1");
    }

    @Test
    void shouldReturnPaginatedRulesFilteredByEventTypeAndEnabled() {
        Pageable pageable = PageRequest.of(0, 10);

        RiskRule rule = RiskRule.builder()
                .ruleId("rule_1")
                .eventType("LOGIN")
                .enabled(true)
                .build();

        RiskRuleResponse response = RiskRuleResponse.builder()
                .ruleId("rule_1")
                .eventType("LOGIN")
                .enabled(true)
                .build();

        PageResponse<RiskRuleResponse> pageResponse = PageResponse.<RiskRuleResponse>builder()
                .content(List.of(response))
                .page(0)
                .size(10)
                .totalElements(1)
                .totalPages(1)
                .last(true)
                .build();

        when(riskRuleRepository.findByTenant_TenantIdAndEventTypeAndEnabled(
                "tenant_1", "LOGIN", true, pageable
        )).thenReturn(new PageImpl<>(List.of(rule), pageable, 1));

        when(riskRuleMapper.toResponse(rule)).thenReturn(response);
        when(pageResponseMapper.toPageResponse(any(Page.class)))
                .thenReturn((PageResponse) pageResponse);

        PageResponse<RiskRuleResponse> result =
                riskRuleService.getRules("tenant_1", "LOGIN", true, pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getRuleId()).isEqualTo("rule_1");
    }
}