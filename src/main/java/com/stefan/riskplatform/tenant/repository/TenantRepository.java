package com.stefan.riskplatform.tenant.repository;

import com.stefan.riskplatform.tenant.entity.Tenant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TenantRepository extends JpaRepository<Tenant, String> {
}