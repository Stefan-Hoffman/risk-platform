package com.stefan.riskplatform.entityrecord.service;

import com.stefan.riskplatform.common.exception.ResourceNotFoundException;
import com.stefan.riskplatform.entityrecord.dto.CreateEntityRecordRequest;
import com.stefan.riskplatform.entityrecord.dto.EntityRecordResponse;
import com.stefan.riskplatform.entityrecord.entity.EntityRecord;
import com.stefan.riskplatform.entityrecord.mapper.EntityRecordMapper;
import com.stefan.riskplatform.entityrecord.repository.EntityRecordRepository;
import com.stefan.riskplatform.tenant.entity.Tenant;
import com.stefan.riskplatform.tenant.service.TenantService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EntityRecordService {

    private final EntityRecordRepository entityRecordRepository;
    private final EntityRecordMapper entityRecordMapper;
    private final TenantService tenantService;

    public EntityRecordResponse createEntityRecord(String tenantId, CreateEntityRecordRequest request) {
        Tenant tenant = tenantService.getTenantOrThrow(tenantId);

        EntityRecord entityRecord = EntityRecord.builder()
                .entityId(request.getEntityId())
                .tenant(tenant)
                .entityType(request.getEntityType())
                .externalRef(request.getExternalRef())
                .createdAt(Instant.now())
                .build();

        EntityRecord savedEntityRecord = entityRecordRepository.save(entityRecord);
        return entityRecordMapper.toResponse(savedEntityRecord);
    }

    public EntityRecord getEntityRecordByTenantOrThrow(String entityId, String tenantId) {
        return entityRecordRepository.findByEntityIdAndTenant_TenantId(entityId, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Entity record not found for tenant. entityId=" + entityId + ", tenantId=" + tenantId
                ));
    }

    public List<EntityRecordResponse> getEntityRecordsByTenant(String tenantId) {
        return entityRecordRepository.findByTenant_TenantId(tenantId)
                .stream()
                .map(entityRecordMapper::toResponse)
                .toList();
    }
}