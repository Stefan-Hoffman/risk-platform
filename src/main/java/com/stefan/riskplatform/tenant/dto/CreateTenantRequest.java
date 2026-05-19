package com.stefan.riskplatform.tenant.dto;

import com.stefan.riskplatform.common.enums.TenantStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class CreateTenantRequest {

    @NotBlank(message = "tenantId is required")
    @Pattern(
            regexp = "^[a-zA-Z0-9_-]+$",
            message = "tenantId may only contain letters, numbers, underscores, and hyphens"
    )
    private String tenantId;

    @NotBlank(message = "name is required")
    private String name;

    @NotNull
    private TenantStatus status;
}