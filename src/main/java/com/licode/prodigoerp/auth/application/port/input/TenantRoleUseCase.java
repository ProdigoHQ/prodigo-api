package com.licode.prodigoerp.auth.application.port.input;

import com.licode.prodigoerp.auth.application.port.input.result.RoleDetailsResult;
import com.licode.prodigoerp.auth.application.port.input.command.SaveRoleCommand;

import java.util.List;
import java.util.UUID;

public interface TenantRoleUseCase {

    RoleDetailsResult create(SaveRoleCommand command);
    RoleDetailsResult update(UUID roleId, SaveRoleCommand command);
    RoleDetailsResult get(UUID roleId);
    List<RoleDetailsResult> list();
    void delete(UUID roleId);
}
