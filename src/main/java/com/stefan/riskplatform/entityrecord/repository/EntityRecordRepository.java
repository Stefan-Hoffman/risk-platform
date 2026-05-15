package com.stefan.riskplatform.entityrecord.repository;

import com.stefan.riskplatform.common.enums.EntityType;
import com.stefan.riskplatform.entityrecord.entity.EntityRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EntityRecordRepository extends JpaRepository<EntityRecord, String> {

    List<EntityRecord> findByTenant_TenantId(String tenantId);

    Optional<EntityRecord> findByEntityIdAndTenant_TenantId(String entityId, String tenantId);

    Page<EntityRecord> findByTenant_TenantId(String tenantId, Pageable pageable);

    Page<EntityRecord> findByTenant_TenantIdAndEntityType(String tenantId, EntityType entityType, Pageable pageable);
}