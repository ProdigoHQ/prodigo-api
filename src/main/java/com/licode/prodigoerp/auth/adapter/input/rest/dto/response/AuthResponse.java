package com.licode.prodigoerp.auth.adapter.input.rest.dto.response;


import java.util.List;
import java.util.UUID;

public record AuthResponse(
        UUID userId,
        String tenantSlug,
        String accessToken,
        List<String> roles,
        List<String> permissions
) {
}
