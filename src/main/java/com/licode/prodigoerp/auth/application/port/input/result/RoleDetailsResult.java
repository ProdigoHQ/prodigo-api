package com.licode.prodigoerp.auth.application.port.input.result;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record RoleDetailsResult(
        UUID id,
        String name,
        String description,
        Instant createdAt,
        List<PermissionSummaryResult> permissions
) {
}
