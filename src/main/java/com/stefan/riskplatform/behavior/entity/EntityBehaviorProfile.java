package com.stefan.riskplatform.behavior.entity;

import com.stefan.riskplatform.entityrecord.entity.EntityRecord;
import com.stefan.riskplatform.tenant.entity.Tenant;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(
        name = "entity_behavior_profiles",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_behavior_profile_tenant_entity",
                        columnNames = {"tenant_id", "entity_id"}
                )
        }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EntityBehaviorProfile {

    @Id
    @Column(name = "profile_id", nullable = false)
    private String profileId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "entity_id", nullable = false)
    private EntityRecord entityRecord;

    @Column(name = "last_seen_at")
    private Instant lastSeenAt;

    @Column(name = "last_ip_address")
    private String lastIpAddress;

    @Column(name = "last_device_id")
    private String lastDeviceId;

    @Column(name = "total_events", nullable = false)
    private Integer totalEvents;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
}