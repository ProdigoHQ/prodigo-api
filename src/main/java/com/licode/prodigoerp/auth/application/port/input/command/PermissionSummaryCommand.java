package com.licode.prodigoerp.auth.application.port.input.command;

import java.util.UUID;

public record PermissionSummaryCommand(
        UUID id,
        String code,
        String description,
        String action,
        String resource
) {
}
