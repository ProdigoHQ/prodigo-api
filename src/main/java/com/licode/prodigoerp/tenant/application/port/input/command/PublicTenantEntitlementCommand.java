package com.licode.prodigoerp.tenant.application.port.input.command;

import java.time.Instant;
import java.util.UUID;

public record PublicTenantEntitlementCommand(
        UUID id,
        Integer maxUsers,
        Integer maxStorageGb,
        Long maxProducts,
        Instant createdAt
) {
}
