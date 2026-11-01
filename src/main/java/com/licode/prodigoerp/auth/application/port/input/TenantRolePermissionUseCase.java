package com.licode.prodigoerp.auth.application.port.input;

import com.licode.prodigoerp.auth.application.port.input.query.ListRolePermissionsQuery;
import com.licode.prodigoerp.auth.application.port.input.result.PermissionSummaryResult;

import java.util.List;
import java.util.UUID;

public interface TenantRolePermissionUseCase {
    List<PermissionSummaryResult> list(ListRolePermissionsQuery query);
    void assign(UUID roleId, UUID permissionId);
    void remove(UUID roleId, UUID permissionId);
}
