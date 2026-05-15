package com.stefan.riskplatform.rule.controller;

import com.stefan.riskplatform.common.dto.PageResponse;
import com.stefan.riskplatform.rule.dto.CreateRiskRuleRequest;
import com.stefan.riskplatform.rule.dto.RiskRuleResponse;
import com.stefan.riskplatform.rule.service.RiskRuleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<PageResponse<RiskRuleResponse>> getRules(
            @RequestHeader("X-Tenant-Id") String tenantId,
            @RequestParam(required = false) String eventType,
            @RequestParam(required = false) Boolean enabled,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt,desc") String[] sort
    ) {
        Pageable pageable = buildPageable(page, size, sort);
        return ResponseEntity.ok(riskRuleService.getRules(tenantId, eventType, enabled, pageable));
    }

    private Pageable buildPageable(int page, int size, String[] sort) {
        String sortField = sort[0];
        Sort.Direction direction = sort.length > 1 && sort[1].equalsIgnoreCase("asc")
                ? Sort.Direction.ASC
                : Sort.Direction.DESC;

        return PageRequest.of(page, size, Sort.by(direction, sortField));
    }
}