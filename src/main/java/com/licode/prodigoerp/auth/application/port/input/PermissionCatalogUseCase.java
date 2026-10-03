package com.licode.prodigoerp.auth.application.port.input;

import com.licode.prodigoerp.auth.application.port.input.command.CreatePermissionCommand;
import com.licode.prodigoerp.auth.application.port.input.command.PermissionSummaryCommand;
import com.licode.prodigoerp.auth.application.port.input.command.UpdatePermissionCommand;

import java.util.List;
import java.util.UUID;

public interface PermissionCatalogUseCase {
    PermissionSummaryCommand create(CreatePermissionCommand command);
    PermissionSummaryCommand update(UUID permissionId, UpdatePermissionCommand  command);
    PermissionSummaryCommand getPermissionById(UUID permissionId);
//    PermissionSummaryCommand getPermissionByCode(String code);
    void delete(UUID permissionId);
    List<PermissionSummaryCommand> listPermissions();
}
