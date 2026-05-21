package com.stefan.riskplatform.behavior.repository;

import com.stefan.riskplatform.behavior.entity.EntityBehaviorProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EntityBehaviorProfileRepository extends JpaRepository<EntityBehaviorProfile, String> {

    Optional<EntityBehaviorProfile> findByTenant_TenantIdAndEntityRecord_EntityId(
            String tenantId,
            String entityId
    );
}