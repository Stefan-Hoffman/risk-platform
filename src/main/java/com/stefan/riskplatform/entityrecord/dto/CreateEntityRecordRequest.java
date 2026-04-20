package com.stefan.riskplatform.entityrecord.dto;

import com.stefan.riskplatform.common.enums.EntityType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateEntityRecordRequest {

    @NotBlank
    private String entityId;

    @NotNull
    private EntityType entityType;

    private String externalRef;
}