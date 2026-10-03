package com.licode.prodigoerp.common.shared.adapter.output;

import com.licode.prodigoerp.common.exception.ForbiddenException;
import com.licode.prodigoerp.common.security.SecurityUtils;
import com.licode.prodigoerp.common.shared.application.output.CurrentUserPort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.UUID;

@Component
public class SecurityCurrentUserAdapter implements CurrentUserPort {
    @Override
    public String username() {

        // protected endpoints: fail loudly if there is no authenticated user
        return SecurityUtils.getCurrentUser().username();
    }

    @Override
    public UUID requireTenantId() {

        UUID tenantId = SecurityUtils.getCurrentUser().tenantId();
        if (tenantId == null) {
            throw new ForbiddenException("This operation requires a tenant account");
        }
        return tenantId;
    }

    @Override
    public boolean hasPermission(String code) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) return false;
        return auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority).filter(Objects::nonNull)
                .anyMatch(a -> a.equals(code));
    }
}
