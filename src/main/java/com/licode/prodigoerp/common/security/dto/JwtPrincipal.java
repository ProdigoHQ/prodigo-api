package com.licode.prodigoerp.common.security.dto;

import java.util.UUID;

public record JwtPrincipal(
        UUID userId,
        String username,
        String email,
        UUID tenantId,
        String tenantSlug
) {}
