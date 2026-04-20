package com.stefan.riskplatform.tenant.dto;

import com.stefan.riskplatform.common.enums.TenantStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateTenantRequest {

    @NotBlank
    private String tenantId;

    @NotBlank
    private String name;

    @NotNull
    private TenantStatus status;
}