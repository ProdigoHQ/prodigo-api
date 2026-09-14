package com.licode.prodigoerp.tenant.application.service;

import com.licode.prodigoerp.common.exception.NotFoundException;
import com.licode.prodigoerp.tenant.application.port.input.TenantEntitlementUseCase;
import com.licode.prodigoerp.tenant.application.port.input.command.PublicTenantEntitlementCommand;
import com.licode.prodigoerp.tenant.application.port.output.TenantEntitlementPort;
import com.licode.prodigoerp.tenant.domain.model.Tenant;
import com.licode.prodigoerp.tenant.domain.model.TenantEntitlement;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TenantEntitlementService implements TenantEntitlementUseCase {
    private final TenantEntitlementPort tenantEntitlementPort;

    @Override
    public TenantEntitlement createDefaultTenantEntitlement(Tenant tenant) {

        TenantEntitlement newEntitlement = new TenantEntitlement();

        newEntitlement.setTenant(tenant);
        newEntitlement.setMaxUsers(5);
        newEntitlement.setMaxStorageGb(5);
        newEntitlement.setMaxProducts(20L);

        Instant now = Instant.now();
        newEntitlement.setCreatedAt(now);
        newEntitlement.setUpdatedAt(now);
        newEntitlement.setCreatedBy(tenant.getCreatedBy());
        newEntitlement.setUpdatedBy(tenant.getUpdatedBy());

        return tenantEntitlementPort.createDefaultTenantEntitlement(newEntitlement);
    }

    @Override
    public PublicTenantEntitlementCommand findEntitlementByUuid(UUID tenantId) {
        Optional<TenantEntitlement> tenantEntitlementOptional = tenantEntitlementPort.findEntitlementById(tenantId);

        if(tenantEntitlementOptional.isEmpty()){
            throw new NotFoundException("Not Tenant Entitlement Found with the Tenant Id: " + tenantId);
        }

        return new PublicTenantEntitlementCommand(
                tenantEntitlementOptional.get().getId(),
                tenantEntitlementOptional.get().getMaxUsers(),
                tenantEntitlementOptional.get().getMaxStorageGb(),
                tenantEntitlementOptional.get().getMaxProducts(),
                tenantEntitlementOptional.get().getCreatedAt()
        );
    }
}
