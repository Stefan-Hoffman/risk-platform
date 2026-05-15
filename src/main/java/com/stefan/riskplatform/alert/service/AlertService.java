package com.stefan.riskplatform.alert.service;

import com.stefan.riskplatform.alert.dto.AlertResponse;
import com.stefan.riskplatform.alert.dto.UpdateAlertStatusRequest;
import com.stefan.riskplatform.alert.entity.Alert;
import com.stefan.riskplatform.alert.mapper.AlertMapper;
import com.stefan.riskplatform.alert.repository.AlertRepository;
import com.stefan.riskplatform.assessment.entity.RiskAssessment;
import com.stefan.riskplatform.common.dto.PageResponse;
import com.stefan.riskplatform.common.enums.AlertStatus;
import com.stefan.riskplatform.common.exception.ResourceNotFoundException;
import com.stefan.riskplatform.common.mapper.PageResponseMapper;
import com.stefan.riskplatform.entityrecord.entity.EntityRecord;
import com.stefan.riskplatform.tenant.entity.Tenant;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AlertService {

    private final AlertRepository alertRepository;
    private final AlertMapper alertMapper;
    private final PageResponseMapper pageResponseMapper;

    public AlertResponse createAlert(Tenant tenant, EntityRecord entityRecord, RiskAssessment assessment, Integer riskScore) {
        Alert alert = Alert.builder()
                .alertId(UUID.randomUUID().toString())
                .tenant(tenant)
                .entityRecord(entityRecord)
                .assessment(assessment)
                .riskScore(riskScore)
                .status(AlertStatus.OPEN)
                .createdAt(Instant.now())
                .build();

        Alert savedAlert = alertRepository.save(alert);
        return alertMapper.toResponse(savedAlert);
    }

    public List<AlertResponse> getAlertsByTenantAndStatus(String tenantId, AlertStatus status) {
        return alertRepository.findByTenant_TenantIdAndStatus(tenantId, status, Pageable.unpaged())
                .stream()
                .map(alertMapper::toResponse)
                .toList();
    }

    public PageResponse<AlertResponse> getAlerts(
            String tenantId,
            AlertStatus status,
            Pageable pageable
    ) {
        Page<Alert> page = (status != null)
                ? alertRepository.findByTenant_TenantIdAndStatus(tenantId, status, pageable)
                : alertRepository.findByTenant_TenantId(tenantId, pageable);

        Page<AlertResponse> mappedPage = page.map(alertMapper::toResponse);
        return pageResponseMapper.toPageResponse(mappedPage);
    }

    public AlertResponse updateAlertStatus(String alertId, UpdateAlertStatusRequest request) {
        Alert alert = alertRepository.findById(alertId)
                .orElseThrow(() -> new ResourceNotFoundException("Alert not found: " + alertId));

        alert.setStatus(request.getStatus());

        Alert updatedAlert = alertRepository.save(alert);
        return alertMapper.toResponse(updatedAlert);
    }
}