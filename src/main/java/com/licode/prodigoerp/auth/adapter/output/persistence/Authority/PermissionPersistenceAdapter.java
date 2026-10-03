package com.licode.prodigoerp.auth.adapter.output.persistence.Authority;

import com.licode.prodigoerp.auth.adapter.output.persistence.Authority.entity.PermissionJpaEntity;
import com.licode.prodigoerp.auth.adapter.output.persistence.Authority.mapper.PermissionJpaMapper;
import com.licode.prodigoerp.auth.adapter.output.persistence.Authority.repository.JpaPermissionRepository;
import com.licode.prodigoerp.auth.application.port.output.PermissionPersistencePort;
import com.licode.prodigoerp.auth.domain.model.Permission;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class PermissionPersistenceAdapter implements PermissionPersistencePort {

    final private JpaPermissionRepository jpaPermissionRepository;

    @Override
    public Permission save(Permission permission) {
        return null;
    }

    @Override
    public Optional<Permission> findById(UUID permissionId) {
        Optional<PermissionJpaEntity> permissionJpaEntity = jpaPermissionRepository
                .findPermissionJpaEntityById(permissionId);

        return permissionJpaEntity.map(PermissionJpaMapper::toDomainModel);
    }

    @Override
    public boolean existsByCode(String code) {

        return jpaPermissionRepository.existsPermissionJpaEntitiesByCodeContainsIgnoreCase(code);
    }

    @Override
    public List<Permission> findAll() {
        return List.of();
    }

    @Override
    public List<Permission> findAllTenantAssignable() {
        return List.of();
    }

    @Override
    public boolean isAssignedToAnyRole(UUID permissionId) {
        return false;
    }

    @Override
    public void delete(Permission permission) {

    }
}
