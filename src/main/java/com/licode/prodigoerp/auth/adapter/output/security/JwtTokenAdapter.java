package com.licode.prodigoerp.auth.adapter.output.security;

import com.licode.prodigoerp.auth.application.port.output.PermissionPersistencePort;
import com.licode.prodigoerp.auth.application.port.output.RolePersistencePort;
import com.licode.prodigoerp.auth.application.port.output.TokenGeneratorPort;
import com.licode.prodigoerp.auth.domain.model.User;
import com.licode.prodigoerp.common.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;


@Component
@RequiredArgsConstructor
public class JwtTokenAdapter implements TokenGeneratorPort {

    private final JwtUtil jwtUtil;
    private final RolePersistencePort rolePersistencePort;
    private final PermissionPersistencePort permissionPersistencePort;


    @Override
    public String generateAccessToken(User user) {

        List<String> roles = rolePersistencePort.findActiveRoleNames(user.getId());
        List<String> permissions = permissionPersistencePort.findActivePermissionCodes(user.getId());

        UUID tenantId = user.getTenant() != null ? user.getTenant().getId() : null;
        String tenantSlug = user.getTenant() != null ? user.getTenant().getSlug() : null;

        return jwtUtil.generateAccessToken(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                tenantId,
                tenantSlug,
                roles,
                permissions
        );
    }
}
