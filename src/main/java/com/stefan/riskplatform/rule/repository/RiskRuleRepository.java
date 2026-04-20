package com.stefan.riskplatform.rule.repository;

import com.stefan.riskplatform.rule.entity.RiskRule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RiskRuleRepository extends JpaRepository<RiskRule, String> {

    List<RiskRule> findByTenant_TenantIdAndEventTypeAndEnabledTrue(
            String tenantId,
            String eventType
    );
}