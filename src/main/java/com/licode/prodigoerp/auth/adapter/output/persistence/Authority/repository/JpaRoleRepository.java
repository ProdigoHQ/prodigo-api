package com.licode.prodigoerp.auth.adapter.output.persistence.Authority.repository;

import com.licode.prodigoerp.auth.adapter.output.persistence.Authority.entity.RoleJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface JpaRoleRepository extends JpaRepository<RoleJpaEntity, UUID> {

    // system roles: tenant is NULL
    Optional<RoleJpaEntity> findRoleJpaEntityByIdAndTenantJpaEntity_IdNull(UUID roleId);
    List<RoleJpaEntity> findAllByTenantJpaEntityNullOrderByNameAsc();
    boolean existsByNameIgnoreCaseAndTenantJpaEntityNull(String name);

    // Tenant roles
    Optional<RoleJpaEntity> findRoleJpaEntityByIdAndTenantJpaEntity_Id(UUID roleId, UUID tenantId);
    List<RoleJpaEntity> findAllByTenantJpaEntity_IdOrderByNameAsc(UUID tenantId);
    boolean existsByNameIgnoreCaseAndTenantJpaEntity_Id(String name, UUID tenantJpaEntityId);
}
