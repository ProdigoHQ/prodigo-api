package com.licode.prodigoerp.auth.application.port.input;

import com.licode.prodigoerp.auth.application.port.input.command.RoleSummaryCommand;
import com.licode.prodigoerp.auth.application.port.input.command.SaveRoleCommand;

import java.util.List;
import java.util.UUID;

public interface SystemRoleUseCase {
    RoleSummaryCommand create(SaveRoleCommand command);
    RoleSummaryCommand update(UUID roleId, SaveRoleCommand command);
    RoleSummaryCommand get(UUID roleId);
    List<RoleSummaryCommand> list();
    void delete(UUID roleId);

    // inspection of a specific tenant (explicit tenantId, never null)
    List<RoleSummaryCommand> listTenantRoles(UUID tenantId);
    RoleSummaryCommand getTenantRole(UUID tenantId, UUID roleId);
}
