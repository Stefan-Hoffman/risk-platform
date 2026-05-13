package com.stefan.riskplatform.integration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stefan.riskplatform.support.IntegrationTestBase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
class RiskPlatformFlowIntegrationTest extends IntegrationTestBase {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldExecuteFullRiskFlow() throws Exception {
        String createTenantRequest = """
                {
                  "tenantId": "tenant_1",
                  "name": "Acme Bank",
                  "status": "ACTIVE"
                }
                """;

        mockMvc.perform(post("/api/v1/tenants")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createTenantRequest))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.tenantId").value("tenant_1"));

        String createEntityRequest = """
                {
                  "entityId": "user_123",
                  "entityType": "USER",
                  "externalRef": "customer-123"
                }
                """;

        mockMvc.perform(post("/api/v1/entities")
                        .header("X-Tenant-Id", "tenant_1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createEntityRequest))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.entityId").value("user_123"));

        String createRuleRequest = """
                {
                  "name": "Suspicious login",
                  "eventType": "LOGIN",
                  "conditionsJson": "{\\"operator\\":\\"AND\\",\\"conditions\\":[{\\"field\\":\\"knownDevice\\",\\"operator\\":\\"EQUALS\\",\\"value\\":false},{\\"field\\":\\"country\\",\\"operator\\":\\"NOT_EQUALS\\",\\"value\\":\\"ZA\\"}]}",
                  "riskScore": 60
                }
                """;

        mockMvc.perform(post("/api/v1/rules")
                        .header("X-Tenant-Id", "tenant_1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createRuleRequest))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Suspicious login"));

        String createEventRequest = """
                {
                  "eventType": "LOGIN",
                  "entityId": "user_123",
                  "source": "web-app",
                  "ipAddress": "192.168.1.4",
                  "deviceId": "device_1",
                  "payload": {
                    "knownDevice": false,
                    "country": "DE"
                  }
                }
                """;

        String eventResponse = mockMvc.perform(post("/api/v1/events")
                        .header("X-Tenant-Id", "tenant_1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createEventRequest))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.eventId").exists())
                .andExpect(jsonPath("$.assessmentId").exists())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode eventJson = objectMapper.readTree(eventResponse);
        String eventId = eventJson.get("eventId").asText();
        String assessmentId = eventJson.get("assessmentId").asText();

        mockMvc.perform(get("/api/v1/events/{eventId}", eventId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.eventId").value(eventId))
                .andExpect(jsonPath("$.entityId").value("user_123"));

        mockMvc.perform(get("/api/v1/assessments/{assessmentId}", assessmentId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.assessmentId").value(assessmentId))
                .andExpect(jsonPath("$.eventId").value(eventId))
                .andExpect(jsonPath("$.totalScore").value(60))
                .andExpect(jsonPath("$.decision").value("REVIEW"))
                .andExpect(jsonPath("$.ruleHits[0].riskScore").value(60));

        String alertsResponse = mockMvc.perform(get("/api/v1/alerts")
                        .header("X-Tenant-Id", "tenant_1")
                        .param("status", "OPEN"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode alertsJson = objectMapper.readTree(alertsResponse);
        assertThat(alertsJson.isArray()).isTrue();
        assertThat(alertsJson.size()).isGreaterThan(0);

        String alertId = alertsJson.get(0).get("alertId").asText();

        String updateAlertRequest = """
                {
                  "status": "RESOLVED"
                }
                """;

        mockMvc.perform(patch("/api/v1/alerts/{alertId}/status", alertId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateAlertRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.alertId").value(alertId))
                .andExpect(jsonPath("$.status").value("RESOLVED"));
    }
}