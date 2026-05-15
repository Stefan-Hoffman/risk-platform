package com.stefan.riskplatform.alert.repository;

import com.stefan.riskplatform.alert.entity.Alert;
import com.stefan.riskplatform.common.enums.AlertStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlertRepository extends JpaRepository<Alert, String> {

    Page<Alert> findByTenant_TenantIdAndStatus(String tenantId, AlertStatus status, Pageable pageable);

    Page<Alert> findByTenant_TenantId(String tenantId, Pageable pageable);
}