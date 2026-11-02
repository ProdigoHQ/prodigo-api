package com.licode.prodigoerp.auth.adapter.input.rest.dto.response;

import java.time.Instant;
import java.util.UUID;

public record RoleSummaryResponse(
        UUID id,
        String name,
        String description,
        Instant createdAt
) {
}
