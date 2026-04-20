package com.stefan.riskplatform.alert.repository;

import com.stefan.riskplatform.alert.entity.Alert;
import com.stefan.riskplatform.common.enums.AlertStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AlertRepository extends JpaRepository<Alert, String> {

    List<Alert> findByTenant_TenantIdAndStatus(String tenantId, AlertStatus status);

    List<Alert> findByEntityRecord_EntityId(String entityId);
}