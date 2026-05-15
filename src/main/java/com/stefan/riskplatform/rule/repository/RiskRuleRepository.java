package com.stefan.riskplatform.rule.repository;

import com.stefan.riskplatform.rule.entity.RiskRule;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RiskRuleRepository extends JpaRepository<RiskRule, String> {

    List<RiskRule> findByTenant_TenantIdAndEventTypeAndEnabledTrue(String tenantId, String eventType);

    Page<RiskRule> findByTenant_TenantId(String tenantId, Pageable pageable);

    Page<RiskRule> findByTenant_TenantIdAndEventType(String tenantId, String eventType, Pageable pageable);

    Page<RiskRule> findByTenant_TenantIdAndEnabled(String tenantId, Boolean enabled, Pageable pageable);

    Page<RiskRule> findByTenant_TenantIdAndEventTypeAndEnabled(
            String tenantId,
            String eventType,
            Boolean enabled,
            Pageable pageable
    );
}