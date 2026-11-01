package com.licode.prodigoerp.auth.application.port.input;

import com.licode.prodigoerp.auth.application.port.input.command.CreatePermissionCommand;
import com.licode.prodigoerp.auth.application.port.input.command.UpdatePermissionCommand;
import com.licode.prodigoerp.auth.application.port.input.query.GetPermissionSummaryQuery;
import com.licode.prodigoerp.auth.application.port.input.result.PermissionSummaryResult;

import java.util.List;
import java.util.UUID;

public interface PermissionCatalogUseCase {
    PermissionSummaryResult create(CreatePermissionCommand command);
    PermissionSummaryResult update(UUID permissionId, UpdatePermissionCommand command);
    PermissionSummaryResult getPermissionById(GetPermissionSummaryQuery query);
    void delete(UUID permissionId);
    List<PermissionSummaryResult> listPermissions();
}
