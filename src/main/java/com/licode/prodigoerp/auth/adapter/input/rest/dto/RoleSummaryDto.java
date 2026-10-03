package com.licode.prodigoerp.auth.adapter.input.rest.dto;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record RoleSummaryDto(
        UUID id,
        String name,
        String description,
        Instant createdAt,
        List<PermissionSummaryDto> permissions

) {
}
