package com.licode.prodigoerp.auth.application.helpers;

import com.licode.prodigoerp.auth.application.port.input.result.PermissionSummaryResult;
import com.licode.prodigoerp.auth.application.port.input.result.RoleDetailsResult;
import com.licode.prodigoerp.auth.application.port.input.result.UserSummaryResult;
import com.licode.prodigoerp.auth.domain.model.Permission;
import com.licode.prodigoerp.auth.domain.model.Role;
import com.licode.prodigoerp.auth.domain.model.User;

import java.util.List;

public class AuthMappings {
    private AuthMappings() {}

    public static PermissionSummaryResult toSummary(Permission p) {
        return new PermissionSummaryResult(p.getId(), p.getCode(), p.getDescription(),
                p.getAction(), p.getResource());
    }

    public static RoleDetailsResult toSummary(Role r, List<Permission> permissions) {
        return new RoleDetailsResult(r.getId(), r.getName(), r.getDescription(),
                r.getCreatedAt(), permissions.stream().map(AuthMappings::toSummary).toList());
    }

    public static UserSummaryResult toUserSummary(User user) {
        return new UserSummaryResult(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getStatus(),
                user.getLastLogin(),
                user.getCreatedAt()
        );
    }
}
