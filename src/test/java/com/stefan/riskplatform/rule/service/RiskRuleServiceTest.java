package com.stefan.riskplatform.rule.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stefan.riskplatform.common.enums.TenantStatus;
import com.stefan.riskplatform.common.exception.DuplicateResourceException;
import com.stefan.riskplatform.common.exception.InvalidRuleDefinitionException;
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
import org.mockito.Spy;
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
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
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

    @Spy
    private ObjectMapper objectMapper = new ObjectMapper();

    private CreateRiskRuleRequest buildRuleRequest(String conditionsJson) {
        CreateRiskRuleRequest request = new CreateRiskRuleRequest();
        request.setName("Bad Rule");
        request.setEventType("LOGIN");
        request.setConditionsJson(conditionsJson);
        request.setRiskScore(60);
        return request;
    }

    private void mockValidTenantAndNoDuplicate(CreateRiskRuleRequest request) {
        Tenant tenant = Tenant.builder()
                .tenantId("tenant_1")
                .build();

        when(tenantService.getTenantOrThrow("tenant_1")).thenReturn(tenant);
        when(riskRuleRepository.existsByTenant_TenantIdAndName("tenant_1", request.getName()))
                .thenReturn(false);
    }

    @Test
    void shouldCreateRiskRule() throws Exception {
        Tenant tenant = Tenant.builder()
                .tenantId("tenant_1")
                .build();

        CreateRiskRuleRequest request = new CreateRiskRuleRequest();
        request.setName("New Device Login");
        request.setEventType("LOGIN");
        request.setConditionsJson("""
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
        """);
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
        when(riskRuleRepository.existsByTenant_TenantIdAndName("tenant_1", "New Device Login"))
                .thenReturn(false);
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

    @Test
    void shouldThrowWhenRiskRuleNameAlreadyExistsForTenant() {
        Tenant tenant = Tenant.builder()
                .tenantId("tenant_1")
                .build();

        CreateRiskRuleRequest request = new CreateRiskRuleRequest();
        request.setName("Suspicious login");
        request.setEventType("LOGIN");
        request.setConditionsJson("""
        {"operator":"AND","conditions":[{"field":"knownDevice","operator":"EQUALS","value":false}]}
        """);
        request.setRiskScore(60);

        when(tenantService.getTenantOrThrow("tenant_1")).thenReturn(tenant);
        when(riskRuleRepository.existsByTenant_TenantIdAndName("tenant_1", "Suspicious login"))
                .thenReturn(true);

        assertThatThrownBy(() -> riskRuleService.createRiskRule("tenant_1", request))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessage("Risk rule already exists for tenant. name=Suspicious login");

        verify(riskRuleRepository, never()).save(any(RiskRule.class));
    }

    @Test
    void shouldThrowWhenConditionsJsonIsInvalid() {
        Tenant tenant = Tenant.builder()
                .tenantId("tenant_1")
                .build();

        CreateRiskRuleRequest request = new CreateRiskRuleRequest();
        request.setName("Bad Rule");
        request.setEventType("LOGIN");
        request.setConditionsJson("{bad-json");
        request.setRiskScore(60);

        when(tenantService.getTenantOrThrow("tenant_1")).thenReturn(tenant);
        when(riskRuleRepository.existsByTenant_TenantIdAndName("tenant_1", "Bad Rule"))
                .thenReturn(false);

        assertThatThrownBy(() -> riskRuleService.createRiskRule("tenant_1", request))
                .isInstanceOf(InvalidRuleDefinitionException.class)
                .hasMessage("Invalid conditionsJson structure");

        verify(riskRuleRepository, never()).save(any(RiskRule.class));
    }

    @Test
    void shouldThrowWhenRuleOperatorIsInvalid() throws Exception {
        Tenant tenant = Tenant.builder()
                .tenantId("tenant_1")
                .build();

        CreateRiskRuleRequest request = new CreateRiskRuleRequest();
        request.setName("Bad Rule");
        request.setEventType("LOGIN");
        request.setRiskScore(50);

        request.setConditionsJson("""
        {
          "operator": "XOR",
          "conditions": [
            {
              "field": "country",
              "operator": "EQUALS",
              "value": "ZA"
            }
          ]
        }
        """);

        JsonNode jsonNode = objectMapper.readTree(request.getConditionsJson());

        when(tenantService.getTenantOrThrow("tenant_1"))
                .thenReturn(tenant);

        when(riskRuleRepository.existsByTenant_TenantIdAndName(
                "tenant_1",
                "Bad Rule"
        )).thenReturn(false);

        when(objectMapper.readTree(request.getConditionsJson()))
                .thenReturn(jsonNode);

        assertThatThrownBy(() ->
                riskRuleService.createRiskRule("tenant_1", request)
        )
                .isInstanceOf(InvalidRuleDefinitionException.class)
                .hasMessage("Top-level operator must be AND or OR");
    }

    @Test
    void shouldThrowWhenRuleMissingTopLevelOperator() throws Exception {
        CreateRiskRuleRequest request = buildRuleRequest("""
        {
          "conditions": [
            {
              "field": "knownDevice",
              "operator": "EQUALS",
              "value": false
            }
          ]
        }
        """);

        mockValidTenantAndNoDuplicate(request);

        assertThatThrownBy(() -> riskRuleService.createRiskRule("tenant_1", request))
                .isInstanceOf(InvalidRuleDefinitionException.class)
                .hasMessage("Rule must contain an operator");

        verify(riskRuleRepository, never()).save(any(RiskRule.class));
    }

    @Test
    void shouldThrowWhenTopLevelOperatorIsInvalid() throws Exception {
        CreateRiskRuleRequest request = buildRuleRequest("""
        {
          "operator": "XOR",
          "conditions": [
            {
              "field": "knownDevice",
              "operator": "EQUALS",
              "value": false
            }
          ]
        }
        """);

        mockValidTenantAndNoDuplicate(request);

        assertThatThrownBy(() -> riskRuleService.createRiskRule("tenant_1", request))
                .isInstanceOf(InvalidRuleDefinitionException.class)
                .hasMessage("Top-level operator must be AND or OR");

        verify(riskRuleRepository, never()).save(any(RiskRule.class));
    }

    @Test
    void shouldThrowWhenRuleMissingConditions() throws Exception {
        CreateRiskRuleRequest request = buildRuleRequest("""
        {
          "operator": "AND"
        }
        """);

        mockValidTenantAndNoDuplicate(request);

        assertThatThrownBy(() -> riskRuleService.createRiskRule("tenant_1", request))
                .isInstanceOf(InvalidRuleDefinitionException.class)
                .hasMessage("Rule must contain conditions");

        verify(riskRuleRepository, never()).save(any(RiskRule.class));
    }

    @Test
    void shouldThrowWhenConditionsArrayIsEmpty() throws Exception {
        CreateRiskRuleRequest request = buildRuleRequest("""
        {
          "operator": "AND",
          "conditions": []
        }
        """);

        mockValidTenantAndNoDuplicate(request);

        assertThatThrownBy(() -> riskRuleService.createRiskRule("tenant_1", request))
                .isInstanceOf(InvalidRuleDefinitionException.class)
                .hasMessage("conditions must be a non-empty array");

        verify(riskRuleRepository, never()).save(any(RiskRule.class));
    }

    @Test
    void shouldThrowWhenConditionMissingField() throws Exception {
        CreateRiskRuleRequest request = buildRuleRequest("""
        {
          "operator": "AND",
          "conditions": [
            {
              "operator": "EQUALS",
              "value": false
            }
          ]
        }
        """);

        mockValidTenantAndNoDuplicate(request);

        assertThatThrownBy(() -> riskRuleService.createRiskRule("tenant_1", request))
                .isInstanceOf(InvalidRuleDefinitionException.class)
                .hasMessage("Condition missing field");

        verify(riskRuleRepository, never()).save(any(RiskRule.class));
    }

    @Test
    void shouldThrowWhenConditionFieldIsBlank() throws Exception {
        CreateRiskRuleRequest request = buildRuleRequest("""
        {
          "operator": "AND",
          "conditions": [
            {
              "field": "",
              "operator": "EQUALS",
              "value": false
            }
          ]
        }
        """);

        mockValidTenantAndNoDuplicate(request);

        assertThatThrownBy(() -> riskRuleService.createRiskRule("tenant_1", request))
                .isInstanceOf(InvalidRuleDefinitionException.class)
                .hasMessage("Condition field cannot be blank");

        verify(riskRuleRepository, never()).save(any(RiskRule.class));
    }

    @Test
    void shouldThrowWhenConditionMissingOperator() throws Exception {
        CreateRiskRuleRequest request = buildRuleRequest("""
        {
          "operator": "AND",
          "conditions": [
            {
              "field": "knownDevice",
              "value": false
            }
          ]
        }
        """);

        mockValidTenantAndNoDuplicate(request);

        assertThatThrownBy(() -> riskRuleService.createRiskRule("tenant_1", request))
                .isInstanceOf(InvalidRuleDefinitionException.class)
                .hasMessage("Condition missing operator");

        verify(riskRuleRepository, never()).save(any(RiskRule.class));
    }

    @Test
    void shouldThrowWhenConditionOperatorIsBlank() throws Exception {
        CreateRiskRuleRequest request = buildRuleRequest("""
        {
          "operator": "AND",
          "conditions": [
            {
              "field": "knownDevice",
              "operator": "",
              "value": false
            }
          ]
        }
        """);

        mockValidTenantAndNoDuplicate(request);

        assertThatThrownBy(() -> riskRuleService.createRiskRule("tenant_1", request))
                .isInstanceOf(InvalidRuleDefinitionException.class)
                .hasMessage("Condition operator cannot be blank");

        verify(riskRuleRepository, never()).save(any(RiskRule.class));
    }

    @Test
    void shouldThrowWhenConditionMissingValue() throws Exception {
        CreateRiskRuleRequest request = buildRuleRequest("""
        {
          "operator": "AND",
          "conditions": [
            {
              "field": "knownDevice",
              "operator": "EQUALS"
            }
          ]
        }
        """);

        mockValidTenantAndNoDuplicate(request);

        assertThatThrownBy(() -> riskRuleService.createRiskRule("tenant_1", request))
                .isInstanceOf(InvalidRuleDefinitionException.class)
                .hasMessage("Condition missing value");

        verify(riskRuleRepository, never()).save(any(RiskRule.class));
    }

    @Test
    void shouldThrowWhenConditionOperatorUnsupported() throws Exception {
        CreateRiskRuleRequest request = buildRuleRequest("""
        {
          "operator": "AND",
          "conditions": [
            {
              "field": "country",
              "operator": "CONTAINS",
              "value": "ZA"
            }
          ]
        }
        """);

        mockValidTenantAndNoDuplicate(request);

        assertThatThrownBy(() -> riskRuleService.createRiskRule("tenant_1", request))
                .isInstanceOf(InvalidRuleDefinitionException.class)
                .hasMessage("Unsupported condition operator: CONTAINS");

        verify(riskRuleRepository, never()).save(any(RiskRule.class));
    }
}