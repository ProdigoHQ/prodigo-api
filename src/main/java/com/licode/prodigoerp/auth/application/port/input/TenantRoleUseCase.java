package com.licode.prodigoerp.auth.application.port.input;

import com.licode.prodigoerp.auth.application.port.input.command.RoleSummaryCommand;
import com.licode.prodigoerp.auth.application.port.input.command.SaveRoleCommand;

import java.util.List;
import java.util.UUID;

public interface TenantRoleUseCase {

    RoleSummaryCommand create(SaveRoleCommand command);
    RoleSummaryCommand update(UUID roleId, SaveRoleCommand command);
    RoleSummaryCommand get(UUID roleId);
    List<RoleSummaryCommand> list();
    void delete(UUID roleId);
}
