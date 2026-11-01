package com.licode.prodigoerp.auth.application.port.input.result;

import java.util.UUID;

public record PermissionSummaryResult(
        UUID id,
        String code,
        String description,
        String action,
        String resource
) {
}
