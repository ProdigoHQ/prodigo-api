package com.licode.prodigoerp.auth.application.port.input.query;

import java.time.Instant;
import java.util.UUID;

public record RoleSummaryQuery(
        UUID id,
        String name,
        String description,
        Instant createdAt
) {
}
