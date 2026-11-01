package com.licode.prodigoerp.auth.application.port.input.query;

import java.util.UUID;

public record GetUserAccessQuery(
        UUID userId
) {
}
