package com.stefan.riskplatform.alert.service;

import com.stefan.riskplatform.alert.dto.AlertResponse;
import com.stefan.riskplatform.alert.dto.UpdateAlertStatusRequest;
import com.stefan.riskplatform.alert.entity.Alert;
import com.stefan.riskplatform.alert.mapper.AlertMapper;
import com.stefan.riskplatform.alert.repository.AlertRepository;
import com.stefan.riskplatform.assessment.entity.RiskAssessment;
import com.stefan.riskplatform.common.enums.AlertStatus;
import com.stefan.riskplatform.common.exception.ResourceNotFoundException;
import com.stefan.riskplatform.entityrecord.entity.EntityRecord;
import com.stefan.riskplatform.tenant.entity.Tenant;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AlertServiceTest {

    @Mock
    private AlertRepository alertRepository;

    @Mock
    private AlertMapper alertMapper;

    @InjectMocks
    private AlertService alertService;

    @Test
    void shouldCreateAlert() {
        Tenant tenant = Tenant.builder().tenantId("tenant_1").build();
        EntityRecord entityRecord = EntityRecord.builder().entityId("user_123").build();
        RiskAssessment assessment = RiskAssessment.builder().assessmentId("assessment_1").build();

        Alert saved = Alert.builder()
                .alertId("alert_1")
                .tenant(tenant)
                .entityRecord(entityRecord)
                .assessment(assessment)
                .riskScore(70)
                .status(AlertStatus.OPEN)
                .createdAt(Instant.now())
                .build();

        AlertResponse response = AlertResponse.builder()
                .alertId("alert_1")
                .tenantId("tenant_1")
                .entityId("user_123")
                .assessmentId("assessment_1")
                .riskScore(70)
                .status(AlertStatus.OPEN)
                .createdAt(saved.getCreatedAt())
                .build();

        when(alertRepository.save(any(Alert.class))).thenReturn(saved);
        when(alertMapper.toResponse(saved)).thenReturn(response);

        AlertResponse result = alertService.createAlert(tenant, entityRecord, assessment, 70);

        assertThat(result.getAlertId()).isEqualTo("alert_1");
        assertThat(result.getStatus()).isEqualTo(AlertStatus.OPEN);
    }

    @Test
    void shouldUpdateAlertStatus() {
        Alert alert = Alert.builder()
                .alertId("alert_1")
                .status(AlertStatus.OPEN)
                .build();

        UpdateAlertStatusRequest request = new UpdateAlertStatusRequest();
        request.setStatus(AlertStatus.RESOLVED);

        Alert updated = Alert.builder()
                .alertId("alert_1")
                .status(AlertStatus.RESOLVED)
                .build();

        AlertResponse response = AlertResponse.builder()
                .alertId("alert_1")
                .status(AlertStatus.RESOLVED)
                .build();

        when(alertRepository.findById("alert_1")).thenReturn(Optional.of(alert));
        when(alertRepository.save(alert)).thenReturn(updated);
        when(alertMapper.toResponse(updated)).thenReturn(response);

        AlertResponse result = alertService.updateAlertStatus("alert_1", request);

        assertThat(result.getStatus()).isEqualTo(AlertStatus.RESOLVED);
    }

    @Test
    void shouldThrowWhenAlertNotFound() {
        UpdateAlertStatusRequest request = new UpdateAlertStatusRequest();
        request.setStatus(AlertStatus.RESOLVED);

        when(alertRepository.findById("alert_404")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> alertService.updateAlertStatus("alert_404", request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Alert not found: alert_404");
    }

    @Test
    void shouldReturnAlertsByTenantAndStatus() {
        Alert alert = Alert.builder().alertId("alert_1").build();
        AlertResponse response = AlertResponse.builder().alertId("alert_1").build();

        when(alertRepository.findByTenant_TenantIdAndStatus("tenant_1", AlertStatus.OPEN))
                .thenReturn(List.of(alert));
        when(alertMapper.toResponse(alert)).thenReturn(response);

        List<AlertResponse> result = alertService.getAlertsByTenantAndStatus("tenant_1", AlertStatus.OPEN);

        assertThat(result).hasSize(1);
    }
}