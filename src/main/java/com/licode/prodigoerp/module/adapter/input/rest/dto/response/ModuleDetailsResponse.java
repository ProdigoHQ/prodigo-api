package com.licode.prodigoerp.module.adapter.input.rest.dto.response;

import com.licode.prodigoerp.auth.adapter.input.rest.dto.response.PermissionSummaryResponse;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record ModuleDetailsResponse(
        UUID id,
        String name,
        String description,
        String moduleKey,
        BigDecimal price,
        String currency,
        Boolean isActive,
        List<PermissionSummaryResponse> permissions
) {
}
