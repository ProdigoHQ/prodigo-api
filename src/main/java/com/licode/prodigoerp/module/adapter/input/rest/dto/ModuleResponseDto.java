package com.licode.prodigoerp.module.adapter.input.rest.dto;

import com.licode.prodigoerp.auth.adapter.input.rest.dto.PermissionSummaryDto;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record ModuleResponseDto(
        UUID id,
        String name,
        String description,
        String moduleKey,
        BigDecimal price,
        String currency,
        Boolean isActive,
        List<PermissionSummaryDto> permissions
) {
}
