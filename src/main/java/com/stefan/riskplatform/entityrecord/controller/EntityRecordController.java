package com.stefan.riskplatform.entityrecord.controller;

import com.stefan.riskplatform.common.dto.PageResponse;
import com.stefan.riskplatform.common.enums.EntityType;
import com.stefan.riskplatform.entityrecord.dto.CreateEntityRecordRequest;
import com.stefan.riskplatform.entityrecord.dto.EntityRecordResponse;
import com.stefan.riskplatform.entityrecord.service.EntityRecordService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<PageResponse<EntityRecordResponse>> getEntityRecords(
            @RequestHeader("X-Tenant-Id") String tenantId,
            @RequestParam(required = false) EntityType entityType,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt,desc") String[] sort
    ) {
        Pageable pageable = buildPageable(page, size, sort);
        return ResponseEntity.ok(entityRecordService.getEntityRecords(tenantId, entityType, pageable));
    }

    private Pageable buildPageable(int page, int size, String[] sort) {
        String sortField = sort[0];
        Sort.Direction direction = sort.length > 1 && sort[1].equalsIgnoreCase("asc")
                ? Sort.Direction.ASC
                : Sort.Direction.DESC;

        return PageRequest.of(page, size, Sort.by(direction, sortField));
    }
}