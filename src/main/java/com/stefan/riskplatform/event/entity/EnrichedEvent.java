package com.stefan.riskplatform.event.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "enriched_events")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EnrichedEvent {

    @Id
    @Column(name = "event_id", nullable = false, length = 64)
    private String eventId;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId
    @JoinColumn(name = "event_id")
    private Event event;

    @Column(name = "country")
    private String country;

    @Column(name = "city")
    private String city;

    @Column(name = "vpn_detected")
    private Boolean vpnDetected;

    @Column(name = "device_fingerprint")
    private String deviceFingerprint;

    @Column(name = "known_device")
    private Boolean knownDevice;

    @Column(name = "asn")
    private String asn;
}