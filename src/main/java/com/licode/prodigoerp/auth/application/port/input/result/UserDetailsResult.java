package com.licode.prodigoerp.auth.application.port.input.result;

import com.licode.prodigoerp.auth.domain.model.AccountStatus;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record UserDetailsResult(
        UUID id,
        String username,
        String email,
        String firstName,
        String lastName,
        AccountStatus status,
        Instant lastLogin,
        Instant createdAt,
        String createBy,
        List<RoleSummaryResult> roles,
        List<PermissionSummaryResult>  permissions
) {
}
