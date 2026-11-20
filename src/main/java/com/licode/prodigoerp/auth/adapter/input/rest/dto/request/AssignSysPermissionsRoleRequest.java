package com.licode.prodigoerp.auth.adapter.input.rest.dto.request;


import java.util.List;
import java.util.UUID;

public record AssignSysPermissionsRoleRequest(
        List<UUID> permissionIds
) {
}
