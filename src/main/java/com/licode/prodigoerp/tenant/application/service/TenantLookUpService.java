package com.licode.prodigoerp.tenant.application.service;

import com.licode.prodigoerp.common.exception.NotFoundException;
import com.licode.prodigoerp.tenant.application.port.input.TenantLookUpUseCase;
import com.licode.prodigoerp.tenant.application.port.output.TenantQueryPort;
import com.licode.prodigoerp.tenant.domain.model.Tenant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class TenantLookUpService implements TenantLookUpUseCase {

    private final TenantQueryPort tenantQueryPort;


    @Override
    public Boolean existsBySlug(String slug) {
        return tenantQueryPort.existsBySlug(slug);
    }

    @Override
    public Tenant findTenantById(UUID tenantId) {
        Optional<Tenant> tenant = tenantQueryPort.findTenantById(tenantId);

        if(tenant.isEmpty()){
            log.error("No Tenant found for tenant id: {}",tenantId);
            throw new NotFoundException("Tenant not found with id " + tenantId);
        }

        return tenant.get();
    }

    // Validate tenant exists and is active
    @Override
    public Boolean isValidTenant(UUID tenantId) {
        return tenantQueryPort.existsByIdAndActive(tenantId);
    }


}
