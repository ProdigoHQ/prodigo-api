package com.licode.prodigoerp.auth.application.port.input.result;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record UserDetailsResult(
        UUID id,
        String username,
        String email,
        String firstName,
        String lastName,
        String status,
        Instant lastLogin,
        Instant createdAt,
        String createBy,
        List<RoleSummaryResult> roles,
        List<PermissionSummaryResult>  permissions
) {
}
