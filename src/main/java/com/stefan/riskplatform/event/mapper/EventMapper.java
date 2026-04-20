package com.stefan.riskplatform.event.mapper;

import com.stefan.riskplatform.event.dto.EventResponse;
import com.stefan.riskplatform.event.entity.Event;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface EventMapper {

    @Mapping(target = "tenantId", source = "tenant.tenantId")
    @Mapping(target = "entityId", source = "entityRecord.entityId")
    EventResponse toResponse(Event event);
}