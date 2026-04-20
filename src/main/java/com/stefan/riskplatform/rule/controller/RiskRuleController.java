package com.stefan.riskplatform.rule.controller;

import com.stefan.riskplatform.rule.dto.CreateRiskRuleRequest;
import com.stefan.riskplatform.rule.dto.RiskRuleResponse;
import com.stefan.riskplatform.rule.service.RiskRuleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/rules")
@RequiredArgsConstructor
public class RiskRuleController {

    private final RiskRuleService riskRuleService;

    @PostMapping
    public ResponseEntity<RiskRuleResponse> createRiskRule(
            @RequestHeader("X-Tenant-Id") String tenantId,
            @Valid @RequestBody CreateRiskRuleRequest request
    ) {
        RiskRuleResponse response = riskRuleService.createRiskRule(tenantId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<RiskRuleResponse>> getEnabledRulesByTenantAndEventType(
            @RequestHeader("X-Tenant-Id") String tenantId,
            @RequestParam String eventType
    ) {
        List<RiskRuleResponse> response =
                riskRuleService.getEnabledRulesByTenantAndEventType(tenantId, eventType);
        return ResponseEntity.ok(response);
    }
}