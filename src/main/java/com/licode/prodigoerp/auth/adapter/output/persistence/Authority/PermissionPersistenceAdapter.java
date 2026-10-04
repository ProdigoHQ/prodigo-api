package com.licode.prodigoerp.auth.adapter.output.persistence.Authority;

import com.licode.prodigoerp.auth.adapter.output.persistence.Authority.entity.PermissionJpaEntity;
import com.licode.prodigoerp.auth.adapter.output.persistence.Authority.mapper.PermissionJpaMapper;
import com.licode.prodigoerp.auth.adapter.output.persistence.Authority.repository.JpaPermissionRepository;
import com.licode.prodigoerp.auth.adapter.output.persistence.Authority.repository.JpaRolePermissionRepository;
import com.licode.prodigoerp.auth.adapter.output.persistence.Authority.repository.JpaUserRoleRepository;
import com.licode.prodigoerp.auth.application.port.output.PermissionPersistencePort;
import com.licode.prodigoerp.auth.domain.model.Permission;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class PermissionPersistenceAdapter implements PermissionPersistencePort {

    final private JpaPermissionRepository jpaPermissionRepository;
    final private JpaUserRoleRepository jpaUserRoleRepository;
    final private JpaRolePermissionRepository jpaRolePermissionRepository;

    @Override
    public Permission save(Permission permission) {

        PermissionJpaEntity jpaEntity = jpaPermissionRepository.save(PermissionJpaMapper.toJpaEntity(permission));
        return PermissionJpaMapper.toDomainModel(jpaEntity);
    }

    @Override
    public Optional<Permission> findById(UUID permissionId) {
        Optional<PermissionJpaEntity> permissionJpaEntity = jpaPermissionRepository
                .findPermissionJpaEntityById(permissionId);

        return permissionJpaEntity.map(PermissionJpaMapper::toDomainModel);
    }

    @Override
    public Optional<Permission> findByCode(String code) {
        Optional<PermissionJpaEntity> permissionJpaEntity = jpaPermissionRepository.findPermissionJpaEntityByCode(code);

        return permissionJpaEntity.map(PermissionJpaMapper::toDomainModel);
    }

    @Override
    public List<Permission> findAllByCodeIn(Collection<String> codes) {
        return jpaPermissionRepository.findAllByCodeIn(codes)
                .stream().map(PermissionJpaMapper::toDomainModel)
                .toList();
    }

    @Override
    public List<Permission> findAllByCodeStartingWith(String prefix) {
        return jpaPermissionRepository.findAllByCodeStartingWith(prefix)
                .stream().map(PermissionJpaMapper::toDomainModel)
                .toList();
    }

    @Override
    public List<Permission> findPermissionsByModuleKey(String moduleKey) {
        List<PermissionJpaEntity> permissionJpaEntityList = jpaPermissionRepository.findPermissionJpaEntityByModuleJpaEntity_ModuleKey(moduleKey);

        return permissionJpaEntityList.stream().map(
                PermissionJpaMapper::toDomainModel
        ).toList();
    }

    @Override
    public Optional<Permission> findByCodeAndResource(String code, String resource) {
        Optional<PermissionJpaEntity> permissionJpaEntity = jpaPermissionRepository.findPermissionJpaEntityByCodeAndResource(code, resource);

        return permissionJpaEntity.map(PermissionJpaMapper::toDomainModel);
    }

    @Override
    public List<String> findActivePermissionCodes(UUID userId) {
        return jpaUserRoleRepository.findActivePermissionCodesByUserId(userId);
    }

    @Override
    public boolean existsByCode(String code) {

        return jpaPermissionRepository.existsPermissionJpaEntitiesByCodeContainsIgnoreCase(code);
    }

    @Override
    public List<Permission> findAll() {
        return jpaPermissionRepository.findAll().stream()
                .map(PermissionJpaMapper::toDomainModel)
                .toList();
    }

    @Override
    public List<Permission> findAllTenantAssignable() {
        return jpaPermissionRepository.findAllByTenantAssignableTrueOrderByCodeAsc().stream()
                .map(PermissionJpaMapper::toDomainModel)
                .toList();
    }

    @Override
    public List<Permission> findPermissionsByRoleId(UUID roleID) {

        List<PermissionJpaEntity> permissionJpaEntityList = jpaRolePermissionRepository.findRolePermissionJpaEntitiesByRoleJpaEntity_Id(roleID);

        return permissionJpaEntityList.stream().map(
                PermissionJpaMapper::toDomainModel
        ).toList();
    }

    @Override
    public boolean isAssignedToAnyRole(UUID permissionId) {
        return jpaRolePermissionRepository.existsByPermissionJpaEntity_Id(permissionId);
    }

    @Override
    public void delete(Permission permission) {
        jpaPermissionRepository.deleteById(permission.getId());
    }
}
