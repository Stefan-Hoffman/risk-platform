package com.stefan.riskplatform.entityrecord.controller;

import com.stefan.riskplatform.entityrecord.dto.CreateEntityRecordRequest;
import com.stefan.riskplatform.entityrecord.dto.EntityRecordResponse;
import com.stefan.riskplatform.entityrecord.service.EntityRecordService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/entities")
@RequiredArgsConstructor
public class EntityRecordController {

    private final EntityRecordService entityRecordService;

    @PostMapping
    public ResponseEntity<EntityRecordResponse> createEntityRecord(
            @RequestHeader("X-Tenant-Id") String tenantId,
            @Valid @RequestBody CreateEntityRecordRequest request
    ) {
        EntityRecordResponse response = entityRecordService.createEntityRecord(tenantId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<EntityRecordResponse>> getEntityRecordsByTenant(
            @RequestHeader("X-Tenant-Id") String tenantId
    ) {
        List<EntityRecordResponse> response = entityRecordService.getEntityRecordsByTenant(tenantId);
        return ResponseEntity.ok(response);
    }
}