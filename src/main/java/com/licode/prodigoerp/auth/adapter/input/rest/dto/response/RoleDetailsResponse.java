package com.licode.prodigoerp.auth.adapter.input.rest.dto.response;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record RoleDetailsResponse(
        UUID id,
        String name,
        String description,
        Instant createdAt,
        List<PermissionSummaryResponse> permissions

) {
}
