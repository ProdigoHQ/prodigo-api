package com.licode.prodigoerp.auth.adapter.input.rest.dto.response;

import com.licode.prodigoerp.auth.domain.model.AccountStatus;

import java.time.Instant;
import java.util.UUID;

public record UserSummaryResponse(
        UUID id,
        String username,
        String email,
        String firstName,
        String lastName,
        AccountStatus status,
        Instant lastLogin,
        Instant createdAt
) {
}
