package com.stefan.riskplatform.entityrecord.dto;

import com.stefan.riskplatform.common.enums.EntityType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateEntityRecordRequest {

    @NotBlank(message = "entityId is required")
    private String entityId;

    @NotNull(message = "entityType is required")
    private EntityType entityType;

    @NotBlank(message = "externalRef is required")
    private String externalRef;
}