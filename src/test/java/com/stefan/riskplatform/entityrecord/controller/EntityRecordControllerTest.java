package com.stefan.riskplatform.entityrecord.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stefan.riskplatform.common.enums.EntityType;
import com.stefan.riskplatform.entityrecord.dto.CreateEntityRecordRequest;
import com.stefan.riskplatform.entityrecord.dto.EntityRecordResponse;
import com.stefan.riskplatform.entityrecord.service.EntityRecordService;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EntityRecordController.class)
class EntityRecordControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private EntityRecordService entityRecordService;

    @Test
    void shouldCreateEntityRecord() throws Exception {
        CreateEntityRecordRequest request = new CreateEntityRecordRequest();
        request.setEntityId("user_123");
        request.setEntityType(EntityType.USER);
        request.setExternalRef("customer-123");

        EntityRecordResponse response = EntityRecordResponse.builder()
                .entityId("user_123")
                .tenantId("tenant_1")
                .entityType(EntityType.USER)
                .externalRef("customer-123")
                .createdAt(Instant.now())
                .build();

        when(entityRecordService.createEntityRecord(eq("tenant_1"), any(CreateEntityRecordRequest.class)))
                .thenReturn(response);

        mockMvc.perform(post("/api/v1/entities")
                        .header("X-Tenant-Id", "tenant_1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.entityId").value("user_123"))
                .andExpect(jsonPath("$.tenantId").value("tenant_1"))
                .andExpect(jsonPath("$.entityType").value("USER"));
    }

    @Test
    void shouldGetEntityRecordsByTenant() throws Exception {
        EntityRecordResponse response = EntityRecordResponse.builder()
                .entityId("user_123")
                .tenantId("tenant_1")
                .entityType(EntityType.USER)
                .createdAt(Instant.now())
                .build();

        when(entityRecordService.getEntityRecordsByTenant("tenant_1"))
                .thenReturn(List.of(response));

        mockMvc.perform(get("/api/v1/entities")
                        .header("X-Tenant-Id", "tenant_1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].entityId").value("user_123"));
    }
}