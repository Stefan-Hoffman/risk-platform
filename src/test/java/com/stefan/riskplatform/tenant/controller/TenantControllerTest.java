package com.stefan.riskplatform.tenant.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stefan.riskplatform.common.enums.TenantStatus;
import com.stefan.riskplatform.tenant.dto.CreateTenantRequest;
import com.stefan.riskplatform.tenant.dto.TenantResponse;
import com.stefan.riskplatform.tenant.service.TenantService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TenantController.class)
class TenantControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TenantService tenantService;

    @Test
    void shouldCreateTenant() throws Exception {
        CreateTenantRequest request = new CreateTenantRequest();
        request.setTenantId("tenant_1");
        request.setName("Acme Bank");
        request.setStatus(TenantStatus.ACTIVE);

        TenantResponse response = TenantResponse.builder()
                .tenantId("tenant_1")
                .name("Acme Bank")
                .status(TenantStatus.ACTIVE)
                .createdAt(Instant.now())
                .build();

        when(tenantService.createTenant(any(CreateTenantRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/tenants")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.tenantId").value("tenant_1"))
                .andExpect(jsonPath("$.name").value("Acme Bank"))
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    @Test
    void shouldReturnBadRequestWhenTenantRequestInvalid() throws Exception {
        CreateTenantRequest request = new CreateTenantRequest();

        mockMvc.perform(post("/api/v1/tenants")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}