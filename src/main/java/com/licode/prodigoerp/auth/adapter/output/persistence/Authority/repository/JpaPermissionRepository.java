package com.licode.prodigoerp.auth.adapter.output.persistence.Authority.repository;

import com.licode.prodigoerp.auth.adapter.output.persistence.Authority.entity.PermissionJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface JpaPermissionRepository extends JpaRepository<PermissionJpaEntity, UUID> {

    Optional<PermissionJpaEntity> findPermissionJpaEntityById(UUID permissionId);
    Optional<PermissionJpaEntity> findPermissionJpaEntityByCode(String code);
    List<PermissionJpaEntity> findPermissionJpaEntityByModuleJpaEntity_ModuleKey(String moduleKey);
}
