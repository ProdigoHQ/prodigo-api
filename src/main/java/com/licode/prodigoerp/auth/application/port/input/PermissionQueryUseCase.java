package com.licode.prodigoerp.auth.application.port.input;

import com.licode.prodigoerp.auth.application.port.input.query.PermissionSummaryQuery;

import java.util.List;
import java.util.UUID;

public interface PermissionQueryUseCase {
    PermissionSummaryQuery get(UUID permissionId);
    List<PermissionSummaryQuery> list();           // tenant-assignable only
}
