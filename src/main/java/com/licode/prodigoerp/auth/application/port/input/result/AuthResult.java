package com.licode.prodigoerp.auth.application.port.input.result;

import java.util.List;
import java.util.UUID;

public record AuthResult(
        UUID userId,
        String tenantSlug,
        String accessToken,
        String refreshToken,
        List<String> roles,
        List<String> permissions
) {
}
