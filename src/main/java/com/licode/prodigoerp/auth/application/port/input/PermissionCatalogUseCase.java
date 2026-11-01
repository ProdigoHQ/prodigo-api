package com.licode.prodigoerp.auth.application.port.input;

import com.licode.prodigoerp.auth.application.port.input.command.CreatePermissionCommand;
import com.licode.prodigoerp.auth.application.port.input.query.PermissionSummaryQuery;
import com.licode.prodigoerp.auth.application.port.input.command.UpdatePermissionCommand;

import java.util.List;
import java.util.UUID;

public interface PermissionCatalogUseCase {
    PermissionSummaryQuery create(CreatePermissionCommand command);
    PermissionSummaryQuery update(UUID permissionId, UpdatePermissionCommand  command);
    PermissionSummaryQuery getPermissionById(UUID permissionId);
//    PermissionSummaryCommand getPermissionByCode(String code);
    void delete(UUID permissionId);
    List<PermissionSummaryQuery> listPermissions();
}
