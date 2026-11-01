package com.licode.prodigoerp.tenant.adapter.input.rest.dto.response;

import java.time.Instant;
import java.util.UUID;

public record TenantEntitlementResponse(
        UUID id,
        Integer maxUsers,
        Integer maxStorageGb,
        Long maxProducts,
        Instant createdAt
) {
}
