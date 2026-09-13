package com.licode.prodigoerp.auth.adapter.input.rest.dto;

import java.util.UUID;

public record PermissionSummaryDto(
        UUID id,
        String code,
        String description,
        String action,
        String resource
) {
}
