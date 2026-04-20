package com.stefan.riskplatform.alert.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stefan.riskplatform.alert.dto.AlertResponse;
import com.stefan.riskplatform.alert.dto.UpdateAlertStatusRequest;
import com.stefan.riskplatform.alert.service.AlertService;
import com.stefan.riskplatform.common.enums.AlertStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AlertController.class)
class AlertControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AlertService alertService;

    @Test
    void shouldGetAlertsByTenantAndStatus() throws Exception {
        AlertResponse response = AlertResponse.builder()
                .alertId("alert_1")
                .tenantId("tenant_1")
                .entityId("user_123")
                .assessmentId("assessment_1")
                .riskScore(70)
                .status(AlertStatus.OPEN)
                .createdAt(Instant.now())
                .build();

        when(alertService.getAlertsByTenantAndStatus("tenant_1", AlertStatus.OPEN))
                .thenReturn(List.of(response));

        mockMvc.perform(get("/api/v1/alerts")
                        .header("X-Tenant-Id", "tenant_1")
                        .param("status", "OPEN"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].alertId").value("alert_1"));
    }

    @Test
    void shouldUpdateAlertStatus() throws Exception {
        UpdateAlertStatusRequest request = new UpdateAlertStatusRequest();
        request.setStatus(AlertStatus.RESOLVED);

        AlertResponse response = AlertResponse.builder()
                .alertId("alert_1")
                .status(AlertStatus.RESOLVED)
                .build();

        when(alertService.updateAlertStatus(eq("alert_1"), any(UpdateAlertStatusRequest.class)))
                .thenReturn(response);

        mockMvc.perform(patch("/api/v1/alerts/alert_1/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.alertId").value("alert_1"))
                .andExpect(jsonPath("$.status").value("RESOLVED"));
    }
}