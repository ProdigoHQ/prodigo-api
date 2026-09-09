package com.licode.prodigoerp.auth.adapter.output.persistence.user;

import com.licode.prodigoerp.tenant.adapter.output.persistence.Tenant.TenantJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface JpaUserRepository extends JpaRepository<UserJpaEntity, UUID> {
    Optional<UserJpaEntity> findByUsername(String username);
    Optional<UserJpaEntity> findByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    Optional<UserJpaEntity> findByIdAndTenant_Id(UUID id, UUID tenantId);
}
