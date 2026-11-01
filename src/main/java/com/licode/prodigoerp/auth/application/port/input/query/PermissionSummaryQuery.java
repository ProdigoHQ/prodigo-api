package com.licode.prodigoerp.auth.application.port.input.query;

import java.util.UUID;

public record PermissionSummaryQuery(
        UUID id,
        String code,
        String description,
        String action,
        String resource
) {
}
