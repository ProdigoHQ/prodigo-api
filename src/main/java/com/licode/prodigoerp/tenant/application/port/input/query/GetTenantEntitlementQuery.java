package com.licode.prodigoerp.tenant.application.port.input.query;

import java.util.UUID;

public record GetTenantEntitlementQuery(
        UUID tenantId
) {
}
