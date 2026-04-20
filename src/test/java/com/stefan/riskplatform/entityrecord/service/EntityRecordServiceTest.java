package com.stefan.riskplatform.entityrecord.service;

import com.stefan.riskplatform.common.enums.EntityType;
import com.stefan.riskplatform.common.enums.TenantStatus;
import com.stefan.riskplatform.common.exception.ResourceNotFoundException;
import com.stefan.riskplatform.entityrecord.dto.CreateEntityRecordRequest;
import com.stefan.riskplatform.entityrecord.dto.EntityRecordResponse;
import com.stefan.riskplatform.entityrecord.entity.EntityRecord;
import com.stefan.riskplatform.entityrecord.mapper.EntityRecordMapper;
import com.stefan.riskplatform.entityrecord.repository.EntityRecordRepository;
import com.stefan.riskplatform.tenant.entity.Tenant;
import com.stefan.riskplatform.tenant.service.TenantService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EntityRecordServiceTest {

    @Mock
    private EntityRecordRepository entityRecordRepository;

    @Mock
    private EntityRecordMapper entityRecordMapper;

    @Mock
    private TenantService tenantService;

    @InjectMocks
    private EntityRecordService entityRecordService;

    @Test
    void shouldCreateEntityRecord() {
        Tenant tenant = Tenant.builder()
                .tenantId("tenant_1")
                .name("Acme Bank")
                .status(TenantStatus.ACTIVE)
                .createdAt(Instant.now())
                .build();

        CreateEntityRecordRequest request = new CreateEntityRecordRequest();
        request.setEntityId("user_123");
        request.setEntityType(EntityType.USER);
        request.setExternalRef("customer-123");

        EntityRecord saved = EntityRecord.builder()
                .entityId("user_123")
                .tenant(tenant)
                .entityType(EntityType.USER)
                .externalRef("customer-123")
                .createdAt(Instant.now())
                .build();

        EntityRecordResponse response = EntityRecordResponse.builder()
                .entityId("user_123")
                .tenantId("tenant_1")
                .entityType(EntityType.USER)
                .externalRef("customer-123")
                .createdAt(saved.getCreatedAt())
                .build();

        when(tenantService.getTenantOrThrow("tenant_1")).thenReturn(tenant);
        when(entityRecordRepository.save(any(EntityRecord.class))).thenReturn(saved);
        when(entityRecordMapper.toResponse(saved)).thenReturn(response);

        EntityRecordResponse result = entityRecordService.createEntityRecord("tenant_1", request);

        assertThat(result.getEntityId()).isEqualTo("user_123");
        assertThat(result.getTenantId()).isEqualTo("tenant_1");
    }

    @Test
    void shouldReturnEntityRecordByTenantWhenFound() {
        EntityRecord entityRecord = EntityRecord.builder()
                .entityId("user_123")
                .build();

        when(entityRecordRepository.findByEntityIdAndTenant_TenantId("user_123", "tenant_1"))
                .thenReturn(Optional.of(entityRecord));

        EntityRecord result = entityRecordService.getEntityRecordByTenantOrThrow("user_123", "tenant_1");

        assertThat(result.getEntityId()).isEqualTo("user_123");
    }

    @Test
    void shouldThrowWhenEntityRecordNotFoundForTenant() {
        when(entityRecordRepository.findByEntityIdAndTenant_TenantId("user_123", "tenant_1"))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                entityRecordService.getEntityRecordByTenantOrThrow("user_123", "tenant_1"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Entity record not found for tenant");
    }

    @Test
    void shouldReturnEntityRecordsByTenant() {
        EntityRecord entityRecord = EntityRecord.builder()
                .entityId("user_123")
                .build();

        EntityRecordResponse response = EntityRecordResponse.builder()
                .entityId("user_123")
                .tenantId("tenant_1")
                .build();

        when(entityRecordRepository.findByTenant_TenantId("tenant_1"))
                .thenReturn(List.of(entityRecord));
        when(entityRecordMapper.toResponse(entityRecord)).thenReturn(response);

        List<EntityRecordResponse> result = entityRecordService.getEntityRecordsByTenant("tenant_1");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getEntityId()).isEqualTo("user_123");
    }
}