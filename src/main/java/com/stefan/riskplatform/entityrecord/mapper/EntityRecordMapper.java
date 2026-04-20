package com.stefan.riskplatform.entityrecord.mapper;

import com.stefan.riskplatform.entityrecord.dto.EntityRecordResponse;
import com.stefan.riskplatform.entityrecord.entity.EntityRecord;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface EntityRecordMapper {

    @Mapping(target = "tenantId", source = "tenant.tenantId")
    EntityRecordResponse toResponse(EntityRecord entityRecord);
}