package com.licode.prodigoerp.auth.application.port.input.query;

import java.util.UUID;

public record GetPermissionSummaryQuery(
        UUID permissionId
) {
}
