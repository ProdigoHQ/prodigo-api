package com.licode.prodigoerp.auth.application.port.input;

import com.licode.prodigoerp.auth.application.port.input.command.PermissionSummaryCommand;

import java.util.List;
import java.util.UUID;

public interface PermissionQueryUseCase {
    PermissionSummaryCommand get(UUID permissionId);
    List<PermissionSummaryCommand> list();           // tenant-assignable only
}
