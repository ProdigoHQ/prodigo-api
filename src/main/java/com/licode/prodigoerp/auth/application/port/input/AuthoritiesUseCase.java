package com.licode.prodigoerp.auth.application.port.input;

import com.licode.prodigoerp.auth.application.port.input.command.AssignRoleCommand;
import com.licode.prodigoerp.auth.application.port.input.command.CreatePermissionCommand;
import com.licode.prodigoerp.auth.application.port.input.command.CreateRoleCommand;
import com.licode.prodigoerp.auth.domain.model.Permission;
import com.licode.prodigoerp.auth.domain.model.Role;

import java.util.UUID;

public interface AuthoritiesUseCase {

    Role saveRole(CreateRoleCommand roleCommand);
    void assignedRoleToUser(AssignRoleCommand assignRoleCommand);
    Permission  savePermission(CreatePermissionCommand permissionCommand, String author);
    void assignedPermissionToRole(UUID permissionId, AssignRoleCommand assignRoleCommand);
}
