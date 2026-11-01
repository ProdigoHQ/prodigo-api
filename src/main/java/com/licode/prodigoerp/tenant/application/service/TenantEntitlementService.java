package com.licode.prodigoerp.tenant.application.service;

import com.licode.prodigoerp.common.exception.NotFoundException;
import com.licode.prodigoerp.tenant.application.port.input.TenantEntitlementUseCase;
import com.licode.prodigoerp.tenant.application.port.input.query.GetTenantEntitlementQuery;
import com.licode.prodigoerp.tenant.application.port.input.result.TenantEntitlementResult;
import com.licode.prodigoerp.tenant.application.port.output.TenantEntitlementPort;
import com.licode.prodigoerp.tenant.domain.model.Tenant;
import com.licode.prodigoerp.tenant.domain.model.TenantEntitlement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class TenantEntitlementService implements TenantEntitlementUseCase {
    private final TenantEntitlementPort tenantEntitlementPort;

    @Override
    public TenantEntitlementResult createDefaultTenantEntitlement(Tenant tenant) {

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

        log.info("Creating default tenant entitlement for tenant: {}",tenant.getName());

        return toResult(tenantEntitlementPort.createDefaultTenantEntitlement(newEntitlement));
    }

    @Override
    public TenantEntitlementResult findEntitlementByUuid(GetTenantEntitlementQuery query) {
        Optional<TenantEntitlement> tenantEntitlementOptional = tenantEntitlementPort.findEntitlementById(query.tenantId());

        if(tenantEntitlementOptional.isEmpty()){
            log.error("No Tenant entitlement found for tenant id: {}", query.tenantId());
            throw new NotFoundException("No Tenant Entitlement Found with the Tenant Id: " + query.tenantId());
        }

        return toResult(tenantEntitlementOptional.get());
    }

    private TenantEntitlementResult toResult(TenantEntitlement entitlement) {
        return new TenantEntitlementResult(
                entitlement.getId(),
                entitlement.getMaxUsers(),
                entitlement.getMaxStorageGb(),
                entitlement.getMaxProducts(),
                entitlement.getCreatedAt()
        );
    }
}
