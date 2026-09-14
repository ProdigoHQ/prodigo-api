package com.licode.prodigoerp.tenant.adapter.input.rest.dto;

import java.time.Instant;
import java.util.UUID;

public record PublicTenantEntitlementDto(
        UUID id,
        Integer maxUsers,
        Integer maxStorageGb,
        Long maxProducts,
        Instant createdAt
) {
}
