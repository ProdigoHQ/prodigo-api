package com.licode.prodigoerp.auth.application.port.output;

import com.licode.prodigoerp.auth.domain.model.User;
import com.licode.prodigoerp.tenant.adapter.output.persistence.Tenant.TenantJpaEntity;

import java.util.Optional;
import java.util.UUID;

public interface LoadUserPort {

    Optional<User> findUserByEmail(String email);
    Optional<User> findUserByUsername(String username);
    Optional<User> findUserById(UUID id);
    Optional<User> findUserByIdAndTenantId(UUID id, UUID tenantId);

}
