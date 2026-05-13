package com.stefan.riskplatform.integration;

import com.stefan.riskplatform.support.IntegrationTestBase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
class RiskPlatformValidationIntegrationTest extends IntegrationTestBase {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldReturnNotFoundWhenTenantDoesNotExistForEntityCreation() throws Exception {
        String request = """
                {
                  "entityId": "user_404",
                  "entityType": "USER",
                  "externalRef": "customer-404"
                }
                """;

        mockMvc.perform(post("/api/v1/entities")
                        .header("X-Tenant-Id", "missing_tenant")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Tenant not found: missing_tenant"));
    }

    @Test
    void shouldReturnBadRequestWhenTenantPayloadInvalid() throws Exception {
        String request = """
                {
                  "tenantId": "",
                  "name": "",
                  "status": null
                }
                """;

        mockMvc.perform(post("/api/v1/tenants")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed"));
    }
}