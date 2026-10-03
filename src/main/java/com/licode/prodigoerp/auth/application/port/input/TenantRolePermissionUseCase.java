package com.licode.prodigoerp.auth.application.port.input;

import com.licode.prodigoerp.auth.application.port.input.command.PermissionSummaryCommand;

import java.util.List;
import java.util.UUID;

public interface TenantRolePermissionUseCase {
    List<PermissionSummaryCommand> list(UUID roleId);
    void assign(UUID roleId, UUID permissionId);
    void remove(UUID roleId, UUID permissionId);
}
