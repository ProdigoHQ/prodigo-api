package com.licode.prodigoerp.auth.application.port.input.command;


import java.util.List;
import java.util.UUID;

public record AssignSysPermissionsRoleCommand(
        List<UUID> permissionIds
) {
}
