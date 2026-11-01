package com.licode.prodigoerp.tenant.application.port.input;

import com.licode.prodigoerp.tenant.application.port.input.query.GetTenantEntitlementQuery;
import com.licode.prodigoerp.tenant.application.port.input.result.TenantEntitlementResult;
import com.licode.prodigoerp.tenant.domain.model.Tenant;

public interface TenantEntitlementUseCase {

    TenantEntitlementResult createDefaultTenantEntitlement(Tenant tenant);
    TenantEntitlementResult findEntitlementByUuid(GetTenantEntitlementQuery query);
}
