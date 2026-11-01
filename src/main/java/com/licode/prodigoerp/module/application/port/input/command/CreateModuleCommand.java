package com.licode.prodigoerp.module.application.port.input.command;



import com.licode.prodigoerp.auth.application.port.input.command.CreatePermissionCommand;

import java.math.BigDecimal;
import java.util.List;

public record CreateModuleCommand(
        String name,
        String description,
        String moduleKey,
        BigDecimal price,
        List<CreatePermissionCommand> createPermissions
) {
}
