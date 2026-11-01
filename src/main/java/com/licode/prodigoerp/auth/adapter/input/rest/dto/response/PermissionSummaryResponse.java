package com.licode.prodigoerp.auth.adapter.input.rest.dto.response;

import java.util.UUID;

public record PermissionSummaryResponse(
        UUID id,
        String code,
        String description,
        String action,
        String resource
) {
}
