package com.stefan.riskplatform.tenant.service;

import com.stefan.riskplatform.common.exception.DuplicateResourceException;
import com.stefan.riskplatform.common.exception.ResourceNotFoundException;
import com.stefan.riskplatform.tenant.dto.CreateTenantRequest;
import com.stefan.riskplatform.tenant.dto.TenantResponse;
import com.stefan.riskplatform.tenant.entity.Tenant;
import com.stefan.riskplatform.tenant.mapper.TenantMapper;
import com.stefan.riskplatform.tenant.repository.TenantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class TenantService {

    private final TenantRepository tenantRepository;
    private final TenantMapper tenantMapper;

    public TenantResponse createTenant(CreateTenantRequest request) {
        if (tenantRepository.existsById(request.getTenantId())) {
            throw new DuplicateResourceException(
                    "Tenant already exists: " + request.getTenantId()
            );
        }

        Tenant tenant = Tenant.builder()
                .tenantId(request.getTenantId())
                .name(request.getName())
                .status(request.getStatus())
                .createdAt(Instant.now())
                .build();

        Tenant savedTenant = tenantRepository.save(tenant);
        return tenantMapper.toResponse(savedTenant);
    }

    public Tenant getTenantOrThrow(String tenantId) {
        return tenantRepository.findById(tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Tenant not found: " + tenantId));
    }
}