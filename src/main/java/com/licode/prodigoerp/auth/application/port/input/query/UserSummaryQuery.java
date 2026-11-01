package com.licode.prodigoerp.auth.application.port.input.query;

import java.time.Instant;
import java.util.UUID;

public record UserSummaryQuery(
        UUID id,
        String username,
        String email,
        String firstName,
        String lastName,
        String status,
        Instant lastLogin,
        Instant createdAt
) {
}
