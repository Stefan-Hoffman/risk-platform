package com.stefan.riskplatform.entityrecord.service;

import com.stefan.riskplatform.common.enums.EntityType;
import com.stefan.riskplatform.common.enums.TenantStatus;
import com.stefan.riskplatform.common.exception.DuplicateResourceException;
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
import com.stefan.riskplatform.common.dto.PageResponse;
import com.stefan.riskplatform.common.mapper.PageResponseMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

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

    @Mock
    private PageResponseMapper pageResponseMapper;

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
        when(entityRecordRepository.existsByEntityIdAndTenant_TenantId("user_123", "tenant_1"))
                .thenReturn(false);

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

    @Test
    void shouldReturnPaginatedEntityRecordsFilteredByType() {
        Pageable pageable = PageRequest.of(0, 10);

        EntityRecord entityRecord = EntityRecord.builder()
                .entityId("user_123")
                .entityType(EntityType.USER)
                .build();

        EntityRecordResponse response = EntityRecordResponse.builder()
                .entityId("user_123")
                .tenantId("tenant_1")
                .entityType(EntityType.USER)
                .build();

        PageResponse<EntityRecordResponse> pageResponse = PageResponse.<EntityRecordResponse>builder()
                .content(List.of(response))
                .page(0)
                .size(10)
                .totalElements(1)
                .totalPages(1)
                .last(true)
                .build();

        when(entityRecordRepository.findByTenant_TenantIdAndEntityType("tenant_1", EntityType.USER, pageable))
                .thenReturn(new PageImpl<>(List.of(entityRecord), pageable, 1));
        when(entityRecordMapper.toResponse(entityRecord)).thenReturn(response);
        when(pageResponseMapper.toPageResponse(any(Page.class)))
                .thenReturn((PageResponse) pageResponse);

        PageResponse<EntityRecordResponse> result =
                entityRecordService.getEntityRecords("tenant_1", EntityType.USER, pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getEntityId()).isEqualTo("user_123");
    }

    @Test
    void shouldThrowWhenEntityAlreadyExistsForTenant() {
        Tenant tenant = Tenant.builder()
                .tenantId("tenant_1")
                .build();

        CreateEntityRecordRequest request = new CreateEntityRecordRequest();
        request.setEntityId("user_123");
        request.setEntityType(EntityType.USER);
        request.setExternalRef("customer-123");

        when(tenantService.getTenantOrThrow("tenant_1")).thenReturn(tenant);
        when(entityRecordRepository.existsByEntityIdAndTenant_TenantId("user_123", "tenant_1"))
                .thenReturn(true);

        assertThatThrownBy(() -> entityRecordService.createEntityRecord("tenant_1", request))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessage("Entity already exists for tenant. entityId=user_123");

        verify(entityRecordRepository, never()).save(any(EntityRecord.class));
    }
}