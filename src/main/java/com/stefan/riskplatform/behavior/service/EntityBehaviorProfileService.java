package com.stefan.riskplatform.behavior.service;

import com.stefan.riskplatform.behavior.entity.EntityBehaviorProfile;
import com.stefan.riskplatform.behavior.repository.EntityBehaviorProfileRepository;
import com.stefan.riskplatform.entityrecord.entity.EntityRecord;
import com.stefan.riskplatform.event.entity.Event;
import com.stefan.riskplatform.tenant.entity.Tenant;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EntityBehaviorProfileService {

    private final EntityBehaviorProfileRepository entityBehaviorProfileRepository;

    public EntityBehaviorProfile updateProfileFromEvent(
            Tenant tenant,
            EntityRecord entityRecord,
            Event event
    ) {
        Instant now = Instant.now();

        EntityBehaviorProfile profile = entityBehaviorProfileRepository
                .findByTenant_TenantIdAndEntityRecord_EntityId(
                        tenant.getTenantId(),
                        entityRecord.getEntityId()
                )
                .orElseGet(() -> createNewProfile(tenant, entityRecord, now));

        profile.setLastSeenAt(event.getEventTimestamp());
        profile.setLastIpAddress(event.getIpAddress());
        profile.setLastDeviceId(event.getDeviceId());
        profile.setTotalEvents(profile.getTotalEvents() + 1);
        profile.setUpdatedAt(now);

        return entityBehaviorProfileRepository.save(profile);
    }

    private EntityBehaviorProfile createNewProfile(
            Tenant tenant,
            EntityRecord entityRecord,
            Instant now
    ) {
        return EntityBehaviorProfile.builder()
                .profileId(UUID.randomUUID().toString())
                .tenant(tenant)
                .entityRecord(entityRecord)
                .totalEvents(0)
                .createdAt(now)
                .updatedAt(now)
                .build();
    }
}