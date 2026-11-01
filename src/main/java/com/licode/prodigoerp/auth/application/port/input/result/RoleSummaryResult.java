package com.licode.prodigoerp.auth.application.port.input.result;

import java.time.Instant;
import java.util.UUID;

public record RoleSummaryResult(
        UUID id,
        String name,
        String description,
        Instant createdAt
) {
}
