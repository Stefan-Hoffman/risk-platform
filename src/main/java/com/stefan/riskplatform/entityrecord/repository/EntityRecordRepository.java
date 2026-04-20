package com.stefan.riskplatform.entityrecord.repository;

import com.stefan.riskplatform.entityrecord.entity.EntityRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EntityRecordRepository extends JpaRepository<EntityRecord, String> {

    List<EntityRecord> findByTenant_TenantId(String tenantId);

    Optional<EntityRecord> findByEntityIdAndTenant_TenantId(String entityId, String tenantId);
}