package com.licode.prodigoerp.tenant.application.port.input.result;

import java.time.Instant;
import java.util.UUID;

public record TenantEntitlementResult(
        UUID id,
        Integer maxUsers,
        Integer maxStorageGb,
        Long maxProducts,
        Instant createdAt
) {
}
