package com.licode.prodigoerp.module.application.port.input.query;

import java.util.UUID;

public record ListTenantActiveModulesQuery(
        UUID tenantId
) {
}
