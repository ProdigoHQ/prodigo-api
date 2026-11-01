package com.licode.prodigoerp.auth.application.port.input;

import com.licode.prodigoerp.auth.application.port.input.command.SaveRoleCommand;
import com.licode.prodigoerp.auth.application.port.input.query.GetRoleDetailsQuery;
import com.licode.prodigoerp.auth.application.port.input.query.GetTenantRoleDetailsQuery;
import com.licode.prodigoerp.auth.application.port.input.query.ListTenantRolesQuery;
import com.licode.prodigoerp.auth.application.port.input.result.RoleDetailsResult;

import java.util.List;
import java.util.UUID;

public interface SystemRoleUseCase {
    RoleDetailsResult create(SaveRoleCommand command);
    RoleDetailsResult update(UUID roleId, SaveRoleCommand command);
    RoleDetailsResult get(GetRoleDetailsQuery query);
    List<RoleDetailsResult> list();
    void delete(UUID roleId);

    // inspection of a specific tenant (explicit tenantId, never null)
    List<RoleDetailsResult> listTenantRoles(ListTenantRolesQuery query);
    RoleDetailsResult getTenantRole(GetTenantRoleDetailsQuery query);
}
