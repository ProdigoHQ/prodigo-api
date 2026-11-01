package com.licode.prodigoerp.auth.adapter.input.rest.dto.request;

import jakarta.validation.constraints.NotBlank;

public record AssignPermissionRequest(
        @NotBlank String permissionCode,
        @NotBlank String roleName,
        @NotBlank Long tenantId
) {
}
