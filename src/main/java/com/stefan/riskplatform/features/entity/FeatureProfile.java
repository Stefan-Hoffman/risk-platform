package com.stefan.riskplatform.features.entity;

import com.stefan.riskplatform.entityrecord.entity.EntityRecord;
import com.stefan.riskplatform.tenant.entity.Tenant;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "feature_profiles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FeatureProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "profile_id")
    private Long profileId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "entity_id", nullable = false)
    private EntityRecord entityRecord;

    @Column(name = "usual_country")
    private String usualCountry;

    @Column(name = "avg_login_hour")
    private Integer avgLoginHour;

    @Column(name = "known_device_count")
    private Integer knownDeviceCount;

    @Column(name = "avg_transaction_amount", precision = 19, scale = 2)
    private BigDecimal avgTransactionAmount;

    @Column(name = "last_updated", nullable = false)
    private Instant lastUpdated;
}