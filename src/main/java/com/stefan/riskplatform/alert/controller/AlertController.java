package com.stefan.riskplatform.alert.controller;

import com.stefan.riskplatform.alert.dto.AlertResponse;
import com.stefan.riskplatform.alert.dto.UpdateAlertStatusRequest;
import com.stefan.riskplatform.alert.service.AlertService;
import com.stefan.riskplatform.common.dto.PageResponse;
import com.stefan.riskplatform.common.enums.AlertStatus;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/alerts")
@RequiredArgsConstructor
public class AlertController {

    private final AlertService alertService;

    @GetMapping
    public ResponseEntity<PageResponse<AlertResponse>> getAlerts(
            @RequestHeader("X-Tenant-Id") String tenantId,
            @RequestParam(required = false) AlertStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt,desc") String[] sort
    ) {
        Pageable pageable = buildPageable(page, size, sort);
        return ResponseEntity.ok(alertService.getAlerts(tenantId, status, pageable));
    }

    @PatchMapping("/{alertId}/status")
    public ResponseEntity<AlertResponse> updateAlertStatus(
            @PathVariable String alertId,
            @Valid @RequestBody UpdateAlertStatusRequest request
    ) {
        AlertResponse response = alertService.updateAlertStatus(alertId, request);
        return ResponseEntity.ok(response);
    }

    private Pageable buildPageable(int page, int size, String[] sort) {
        String sortField = sort[0];
        Sort.Direction direction = sort.length > 1 && sort[1].equalsIgnoreCase("asc")
                ? Sort.Direction.ASC
                : Sort.Direction.DESC;

        return PageRequest.of(page, size, Sort.by(direction, sortField));
    }
}