package com.licode.prodigoerp.tenant.application.port.output;

import com.licode.prodigoerp.tenant.domain.model.TenantEntitlement;

import java.util.Optional;
import java.util.UUID;

public interface TenantEntitlementPort {

    TenantEntitlement createDefaultTenantEntitlement(TenantEntitlement tenantEntitlement);
    Optional<TenantEntitlement> findEntitlementById(UUID tenantId);
}
