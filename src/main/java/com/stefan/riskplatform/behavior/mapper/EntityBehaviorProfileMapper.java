package com.stefan.riskplatform.behavior.mapper;

import com.stefan.riskplatform.behavior.dto.EntityBehaviorProfileResponse;
import com.stefan.riskplatform.behavior.entity.EntityBehaviorProfile;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface EntityBehaviorProfileMapper {

    @Mapping(target = "tenantId", source = "tenant.tenantId")
    @Mapping(target = "entityId", source = "entityRecord.entityId")
    EntityBehaviorProfileResponse toResponse(EntityBehaviorProfile profile);
}