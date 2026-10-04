package com.licode.prodigoerp.auth.adapter.output.persistence.Authority.repository;

import com.licode.prodigoerp.auth.adapter.output.persistence.Authority.entity.PermissionJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface JpaPermissionRepository extends JpaRepository<PermissionJpaEntity, UUID> {

    Optional<PermissionJpaEntity> findPermissionJpaEntityById(UUID permissionId);
    Optional<PermissionJpaEntity> findPermissionJpaEntityByCode(String code);
    Boolean existsPermissionJpaEntitiesByCodeContainsIgnoreCase(String code);
    List<PermissionJpaEntity> findPermissionJpaEntityByModuleJpaEntity_ModuleKey(String moduleKey);
    Optional<PermissionJpaEntity> findPermissionJpaEntityByCodeAndResource(String code, String resource);
    List<PermissionJpaEntity> findAllByCodeIn(Collection<String> codes);
    List<PermissionJpaEntity> findAllByCodeStartingWith(String prefix);
    List<PermissionJpaEntity> findAllByTenantAssignableTrueOrderByCodeAsc();
}
