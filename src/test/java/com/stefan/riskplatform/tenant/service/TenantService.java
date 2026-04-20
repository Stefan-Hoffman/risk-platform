package com.stefan.riskplatform.tenant.service;

import com.stefan.riskplatform.common.enums.TenantStatus;
import com.stefan.riskplatform.common.exception.ResourceNotFoundException;
import com.stefan.riskplatform.tenant.dto.CreateTenantRequest;
import com.stefan.riskplatform.tenant.dto.TenantResponse;
import com.stefan.riskplatform.tenant.entity.Tenant;
import com.stefan.riskplatform.tenant.mapper.TenantMapper;
import com.stefan.riskplatform.tenant.repository.TenantRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TenantServiceTest {

    @Mock
    private TenantRepository tenantRepository;

    @Mock
    private TenantMapper tenantMapper;

    @InjectMocks
    private TenantService tenantService;

    @Test
    void shouldCreateTenant() {
        CreateTenantRequest request = new CreateTenantRequest();
        request.setTenantId("tenant_1");
        request.setName("Acme Bank");
        request.setStatus(TenantStatus.ACTIVE);

        Tenant savedTenant = Tenant.builder()
                .tenantId("tenant_1")
                .name("Acme Bank")
                .status(TenantStatus.ACTIVE)
                .createdAt(Instant.now())
                .build();

        TenantResponse response = TenantResponse.builder()
                .tenantId("tenant_1")
                .name("Acme Bank")
                .status(TenantStatus.ACTIVE)
                .createdAt(savedTenant.getCreatedAt())
                .build();

        when(tenantRepository.save(any(Tenant.class))).thenReturn(savedTenant);
        when(tenantMapper.toResponse(savedTenant)).thenReturn(response);

        TenantResponse result = tenantService.createTenant(request);

        assertThat(result.getTenantId()).isEqualTo("tenant_1");
        assertThat(result.getName()).isEqualTo("Acme Bank");

        verify(tenantRepository).save(any(Tenant.class));
        verify(tenantMapper).toResponse(savedTenant);
    }

    @Test
    void shouldReturnTenantWhenFound() {
        Tenant tenant = Tenant.builder()
                .tenantId("tenant_1")
                .name("Acme Bank")
                .status(TenantStatus.ACTIVE)
                .createdAt(Instant.now())
                .build();

        when(tenantRepository.findById("tenant_1")).thenReturn(Optional.of(tenant));

        Tenant result = tenantService.getTenantOrThrow("tenant_1");

        assertThat(result.getTenantId()).isEqualTo("tenant_1");
    }

    @Test
    void shouldThrowWhenTenantNotFound() {
        when(tenantRepository.findById("tenant_404")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> tenantService.getTenantOrThrow("tenant_404"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Tenant not found: tenant_404");
    }
}