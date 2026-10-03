package com.licode.prodigoerp.auth.adapter.output.persistence.Authority.repository;

import com.licode.prodigoerp.auth.adapter.output.persistence.Authority.entity.RoleJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface JpaRoleRepository extends JpaRepository<RoleJpaEntity, UUID> {

    Optional<RoleJpaEntity> findRoleJpaEntitiesByIdAndTenantJpaEntity_Id(UUID roleId, UUID tenantId);
    boolean existsByName(String roleName);

    Optional<RoleJpaEntity> findRoleJpaEntitiesByNameAndTenantJpaEntity_IdNull(String roleName);
    Optional<RoleJpaEntity> findRoleJpaEntityByIdAndTenantJpaEntity_IdNull(UUID roleId);

    Optional<RoleJpaEntity> findRoleJpaEntityById(UUID id);
}
