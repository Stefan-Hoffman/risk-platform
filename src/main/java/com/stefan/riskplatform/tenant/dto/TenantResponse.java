package com.stefan.riskplatform.tenant.dto;

import com.stefan.riskplatform.common.enums.TenantStatus;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class TenantResponse {
    private String tenantId;
    private String name;
    private TenantStatus status;
    private Instant createdAt;
}