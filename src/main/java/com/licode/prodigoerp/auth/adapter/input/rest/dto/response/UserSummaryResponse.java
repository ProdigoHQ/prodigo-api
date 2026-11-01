package com.licode.prodigoerp.auth.adapter.input.rest.dto.response;

import java.time.Instant;
import java.util.UUID;

public record UserSummaryResponse(
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
