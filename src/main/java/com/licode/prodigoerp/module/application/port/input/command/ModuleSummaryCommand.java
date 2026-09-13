package com.licode.prodigoerp.module.application.port.input.command;

import com.licode.prodigoerp.auth.application.port.input.command.PermissionSummaryCommand;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record ModuleSummaryCommand(
        UUID id,
        String name,
        String description,
        String moduleKey,
        BigDecimal price,
        String currency,
        Boolean isActive,
        List<PermissionSummaryCommand> permissions
) {
}
