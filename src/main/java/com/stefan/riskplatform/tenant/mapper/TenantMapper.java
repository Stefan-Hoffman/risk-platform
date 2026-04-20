package com.stefan.riskplatform.tenant.mapper;

import com.stefan.riskplatform.tenant.dto.TenantResponse;
import com.stefan.riskplatform.tenant.entity.Tenant;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TenantMapper {

    TenantResponse toResponse(Tenant tenant);
}