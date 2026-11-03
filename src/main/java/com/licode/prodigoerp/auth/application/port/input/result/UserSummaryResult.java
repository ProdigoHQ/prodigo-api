package com.licode.prodigoerp.auth.application.port.input.result;

import com.licode.prodigoerp.auth.domain.model.AccountStatus;

import java.time.Instant;
import java.util.UUID;

public record UserSummaryResult(
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
