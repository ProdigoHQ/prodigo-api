package com.licode.prodigoerp.auth.application.helpers;

import com.licode.prodigoerp.auth.application.port.input.command.PermissionSummaryCommand;
import com.licode.prodigoerp.auth.application.port.input.command.RoleSummaryCommand;
import com.licode.prodigoerp.auth.domain.model.Permission;
import com.licode.prodigoerp.auth.domain.model.Role;

import java.util.List;

public class AuthMappings {
    private AuthMappings() {}

    public static PermissionSummaryCommand toSummary(Permission p) {
        return new PermissionSummaryCommand(p.getId(), p.getCode(), p.getDescription(),
                p.getAction(), p.getResource());
    }

    public static RoleSummaryCommand toSummary(Role r, List<Permission> permissions) {
        return new RoleSummaryCommand(r.getId(), r.getName(), r.getDescription(),
                r.getCreatedAt(), permissions.stream().map(AuthMappings::toSummary).toList());
    }
}
