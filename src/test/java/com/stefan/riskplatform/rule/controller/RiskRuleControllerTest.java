package com.stefan.riskplatform.rule.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stefan.riskplatform.rule.dto.CreateRiskRuleRequest;
import com.stefan.riskplatform.rule.dto.RiskRuleResponse;
import com.stefan.riskplatform.rule.service.RiskRuleService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import com.stefan.riskplatform.common.dto.PageResponse;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.ArgumentMatchers.*;

@WebMvcTest(RiskRuleController.class)
class RiskRuleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private RiskRuleService riskRuleService;

    @Test
    void shouldCreateRiskRule() throws Exception {
        CreateRiskRuleRequest request = new CreateRiskRuleRequest();
        request.setName("New Device Login");
        request.setEventType("LOGIN");
        request.setConditionsJson("{\"field\":\"knownDevice\",\"value\":false}");
        request.setRiskScore(40);

        RiskRuleResponse response = RiskRuleResponse.builder()
                .ruleId("rule_1")
                .tenantId("tenant_1")
                .name("New Device Login")
                .eventType("LOGIN")
                .conditionsJson(request.getConditionsJson())
                .riskScore(40)
                .enabled(true)
                .version(1)
                .createdAt(Instant.now())
                .build();

        when(riskRuleService.createRiskRule(eq("tenant_1"), any(CreateRiskRuleRequest.class)))
                .thenReturn(response);

        mockMvc.perform(post("/api/v1/rules")
                        .header("X-Tenant-Id", "tenant_1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.ruleId").value("rule_1"))
                .andExpect(jsonPath("$.eventType").value("LOGIN"));
    }

    @Test
    void shouldGetPaginatedRulesByEventTypeAndEnabled() throws Exception {
        RiskRuleResponse rule = RiskRuleResponse.builder()
                .ruleId("rule_1")
                .tenantId("tenant_1")
                .name("New Device Login")
                .eventType("LOGIN")
                .conditionsJson("{\"field\":\"knownDevice\",\"value\":false}")
                .riskScore(40)
                .enabled(true)
                .version(1)
                .createdAt(Instant.now())
                .build();

        PageResponse<RiskRuleResponse> response = PageResponse.<RiskRuleResponse>builder()
                .content(List.of(rule))
                .page(0)
                .size(10)
                .totalElements(1)
                .totalPages(1)
                .last(true)
                .build();

        when(riskRuleService.getRules(eq("tenant_1"), eq("LOGIN"), eq(true), any(Pageable.class)))
                .thenReturn(response);

        mockMvc.perform(get("/api/v1/rules")
                        .header("X-Tenant-Id", "tenant_1")
                        .param("eventType", "LOGIN")
                        .param("enabled", "true")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sort", "createdAt,desc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].ruleId").value("rule_1"))
                .andExpect(jsonPath("$.content[0].eventType").value("LOGIN"))
                .andExpect(jsonPath("$.content[0].enabled").value(true))
                .andExpect(jsonPath("$.totalElements").value(1));
    }
}