package com.licode.prodigoerp.auth.application.port.input;

import com.licode.prodigoerp.auth.application.port.input.result.RoleDetailsResult;
import com.licode.prodigoerp.auth.application.port.input.command.SaveRoleCommand;

import java.util.List;
import java.util.UUID;

public interface SystemRoleUseCase {
    RoleDetailsResult create(SaveRoleCommand command);
    RoleDetailsResult update(UUID roleId, SaveRoleCommand command);
    RoleDetailsResult get(UUID roleId);
    List<RoleDetailsResult> list();
    void delete(UUID roleId);

    // inspection of a specific tenant (explicit tenantId, never null)
    List<RoleDetailsResult> listTenantRoles(UUID tenantId);
    RoleDetailsResult getTenantRole(UUID tenantId, UUID roleId);
}
