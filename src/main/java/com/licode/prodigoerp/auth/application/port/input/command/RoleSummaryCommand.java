package com.licode.prodigoerp.auth.application.port.input.command;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record RoleSummaryCommand(
        UUID id,
        String name,
        String description,
        Instant createdAt,
        List<PermissionSummaryCommand> permissions
) {
}
