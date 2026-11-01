package com.licode.prodigoerp.module.application.port.input.result;

import com.licode.prodigoerp.auth.application.port.input.result.PermissionSummaryResult;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record ModuleDetailsResult(
        UUID id,
        String name,
        String description,
        String moduleKey,
        BigDecimal price,
        String currency,
        Boolean isActive,
        List<PermissionSummaryResult> permissions
) {
}
