package com.stefan.riskplatform.alert.controller;

import com.stefan.riskplatform.alert.dto.AlertResponse;
import com.stefan.riskplatform.alert.dto.UpdateAlertStatusRequest;
import com.stefan.riskplatform.alert.service.AlertService;
import com.stefan.riskplatform.common.enums.AlertStatus;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/alerts")
@RequiredArgsConstructor
public class AlertController {

    private final AlertService alertService;

    @GetMapping
    public ResponseEntity<List<AlertResponse>> getAlertsByTenantAndStatus(
            @RequestHeader("X-Tenant-Id") String tenantId,
            @RequestParam AlertStatus status
    ) {
        List<AlertResponse> response = alertService.getAlertsByTenantAndStatus(tenantId, status);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{alertId}/status")
    public ResponseEntity<AlertResponse> updateAlertStatus(
            @PathVariable String alertId,
            @Valid @RequestBody UpdateAlertStatusRequest request
    ) {
        AlertResponse response = alertService.updateAlertStatus(alertId, request);
        return ResponseEntity.ok(response);
    }
}