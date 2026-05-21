package com.stefan.riskplatform.behavior.service;

import com.stefan.riskplatform.behavior.entity.EntityBehaviorProfile;
import com.stefan.riskplatform.behavior.repository.EntityBehaviorProfileRepository;
import com.stefan.riskplatform.entityrecord.entity.EntityRecord;
import com.stefan.riskplatform.event.entity.Event;
import com.stefan.riskplatform.tenant.entity.Tenant;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EntityBehaviorProfileServiceTest {

    @Mock
    private EntityBehaviorProfileRepository entityBehaviorProfileRepository;

    @InjectMocks
    private EntityBehaviorProfileService entityBehaviorProfileService;

    @Test
    void shouldCreateProfileWhenNoneExists() {
        Tenant tenant = Tenant.builder()
                .tenantId("tenant_1")
                .build();

        EntityRecord entityRecord = EntityRecord.builder()
                .entityId("user_123")
                .build();

        Event event = Event.builder()
                .eventId("event_1")
                .eventTimestamp(Instant.now())
                .ipAddress("192.168.1.4")
                .deviceId("device_1")
                .build();

        when(entityBehaviorProfileRepository.findByTenant_TenantIdAndEntityRecord_EntityId(
                "tenant_1",
                "user_123"
        )).thenReturn(Optional.empty());

        when(entityBehaviorProfileRepository.save(any(EntityBehaviorProfile.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        EntityBehaviorProfile result = entityBehaviorProfileService.updateProfileFromEvent(
                tenant,
                entityRecord,
                event
        );

        assertThat(result.getProfileId()).isNotBlank();
        assertThat(result.getTenant()).isEqualTo(tenant);
        assertThat(result.getEntityRecord()).isEqualTo(entityRecord);
        assertThat(result.getTotalEvents()).isEqualTo(1);
        assertThat(result.getLastIpAddress()).isEqualTo("192.168.1.4");
        assertThat(result.getLastDeviceId()).isEqualTo("device_1");

        verify(entityBehaviorProfileRepository).save(any(EntityBehaviorProfile.class));
    }

    @Test
    void shouldUpdateExistingProfile() {
        Tenant tenant = Tenant.builder()
                .tenantId("tenant_1")
                .build();

        EntityRecord entityRecord = EntityRecord.builder()
                .entityId("user_123")
                .build();

        EntityBehaviorProfile existingProfile = EntityBehaviorProfile.builder()
                .profileId("profile_1")
                .tenant(tenant)
                .entityRecord(entityRecord)
                .totalEvents(3)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        Event event = Event.builder()
                .eventId("event_1")
                .eventTimestamp(Instant.now())
                .ipAddress("10.0.0.1")
                .deviceId("device_2")
                .build();

        when(entityBehaviorProfileRepository.findByTenant_TenantIdAndEntityRecord_EntityId(
                "tenant_1",
                "user_123"
        )).thenReturn(Optional.of(existingProfile));

        when(entityBehaviorProfileRepository.save(existingProfile))
                .thenReturn(existingProfile);

        EntityBehaviorProfile result = entityBehaviorProfileService.updateProfileFromEvent(
                tenant,
                entityRecord,
                event
        );

        assertThat(result.getProfileId()).isEqualTo("profile_1");
        assertThat(result.getTotalEvents()).isEqualTo(4);
        assertThat(result.getLastIpAddress()).isEqualTo("10.0.0.1");
        assertThat(result.getLastDeviceId()).isEqualTo("device_2");

        verify(entityBehaviorProfileRepository).save(existingProfile);
    }
}