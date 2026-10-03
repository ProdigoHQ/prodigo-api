package com.licode.prodigoerp.auth.application.port.input;

import com.licode.prodigoerp.auth.application.port.input.command.*;
import com.licode.prodigoerp.auth.domain.model.Permission;
import com.licode.prodigoerp.auth.domain.model.Role;

import java.util.UUID;

public interface AuthoritiesUseCase {

    Role saveRole(CreateRoleCommand roleCommand);
    void assignedRoleToUser(AssignRoleCommand assignRoleCommand);
    Permission  savePermission(CreatePermissionCommand permissionCommand, String author);
    void assignedPermissionToRole(UUID permissionId, AssignRoleCommand assignRoleCommand);
    PermissionSummaryCommand fetchPermissionSummary(UUID permissionId);
    RoleSummaryCommand fetchRoleSummary(UUID roleId, UUID tenantId);
    void deletePermission(UUID permissionId);
}
