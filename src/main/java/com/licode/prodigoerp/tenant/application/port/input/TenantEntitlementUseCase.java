package com.licode.prodigoerp.tenant.application.port.input;

import com.licode.prodigoerp.tenant.application.port.input.command.PublicTenantEntitlementCommand;
import com.licode.prodigoerp.tenant.domain.model.Tenant;
import com.licode.prodigoerp.tenant.domain.model.TenantEntitlement;

import java.util.UUID;

public interface TenantEntitlementUseCase {

    TenantEntitlement createDefaultTenantEntitlement(Tenant tenant);
    PublicTenantEntitlementCommand findEntitlementByUuid(UUID tenantId);
}
