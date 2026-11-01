package com.licode.prodigoerp.auth.application.port.input.query;

import java.util.UUID;

public record GetTenantRoleDetailsQuery(
        UUID tenantId,
        UUID roleId
) {
}
