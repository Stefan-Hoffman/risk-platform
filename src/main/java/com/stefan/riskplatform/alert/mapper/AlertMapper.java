package com.stefan.riskplatform.alert.mapper;

import com.stefan.riskplatform.alert.dto.AlertResponse;
import com.stefan.riskplatform.alert.entity.Alert;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AlertMapper {

    @Mapping(target = "tenantId", source = "tenant.tenantId")
    @Mapping(target = "entityId", source = "entityRecord.entityId")
    @Mapping(target = "assessmentId", source = "assessment.assessmentId")
    AlertResponse toResponse(Alert alert);
}