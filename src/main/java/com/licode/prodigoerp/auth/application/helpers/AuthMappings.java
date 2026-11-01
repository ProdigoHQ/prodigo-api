package com.licode.prodigoerp.auth.application.helpers;

import com.licode.prodigoerp.auth.application.port.input.query.PermissionSummaryQuery;
import com.licode.prodigoerp.auth.application.port.input.result.RoleDetailsResult;
import com.licode.prodigoerp.auth.domain.model.Permission;
import com.licode.prodigoerp.auth.domain.model.Role;

import java.util.List;

public class AuthMappings {
    private AuthMappings() {}

    public static PermissionSummaryQuery toSummary(Permission p) {
        return new PermissionSummaryQuery(p.getId(), p.getCode(), p.getDescription(),
                p.getAction(), p.getResource());
    }

    public static RoleDetailsResult toSummary(Role r, List<Permission> permissions) {
        return new RoleDetailsResult(r.getId(), r.getName(), r.getDescription(),
                r.getCreatedAt(), permissions.stream().map(AuthMappings::toSummary).toList());
    }
}
