package com.licode.prodigoerp.auth.adapter.output.persistence.Authority;

import com.licode.prodigoerp.auth.adapter.output.persistence.Authority.entity.PermissionJpaEntity;
import com.licode.prodigoerp.auth.adapter.output.persistence.Authority.entity.RoleJpaEntity;
import com.licode.prodigoerp.auth.adapter.output.persistence.Authority.entity.RolePermissionJpaEntity;
import com.licode.prodigoerp.auth.adapter.output.persistence.Authority.entity.UserRoleJpaEntity;
import com.licode.prodigoerp.auth.adapter.output.persistence.Authority.mapper.PermissionJpaMapper;
import com.licode.prodigoerp.auth.adapter.output.persistence.Authority.mapper.RoleJpaMapper;
import com.licode.prodigoerp.auth.adapter.output.persistence.Authority.mapper.RolePermissionJpaMapper;
import com.licode.prodigoerp.auth.adapter.output.persistence.Authority.mapper.UserRoleJpaMapper;
import com.licode.prodigoerp.auth.adapter.output.persistence.Authority.repository.JpaPermissionRepository;
import com.licode.prodigoerp.auth.adapter.output.persistence.Authority.repository.JpaRolePermissionRepository;
import com.licode.prodigoerp.auth.adapter.output.persistence.Authority.repository.JpaRoleRepository;
import com.licode.prodigoerp.auth.adapter.output.persistence.Authority.repository.JpaUserRoleRepository;
import com.licode.prodigoerp.auth.application.port.output.RoleQueryPort;
import com.licode.prodigoerp.auth.application.port.output.SavePermissionPort;
import com.licode.prodigoerp.auth.application.port.output.SaveRolePort;
import com.licode.prodigoerp.auth.domain.model.Permission;
import com.licode.prodigoerp.auth.domain.model.Role;
import com.licode.prodigoerp.auth.domain.model.RolePermission;
import com.licode.prodigoerp.auth.domain.model.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class RolePersistenceAdapter implements RoleQueryPort, SaveRolePort, SavePermissionPort {
    final private JpaRoleRepository jpaRoleRepository;
    final private JpaUserRoleRepository jpaUserRoleRepository;
    final private JpaPermissionRepository jpaPermissionRepository;
    final private JpaRolePermissionRepository jpaRolePermissionRepository;

    @Override
    public List<String> findActiveRoleNames(UUID userId) {

        return jpaUserRoleRepository.findActiveRoleNamesByUserId(userId);
    }

    @Override
    public List<String> findActivePermissionCodes(UUID userId) {
        return jpaUserRoleRepository.findActivePermissionCodesByUserId(userId);
    }

    @Override
    public Optional<Role> findRoleByIdAndTenantId(UUID roleId, UUID tenantId) {
        Optional<RoleJpaEntity> roleJpaEntity =  jpaRoleRepository.findRoleJpaEntitiesByIdAndTenantJpaEntity_Id(roleId, tenantId);

        return roleJpaEntity.map(RoleJpaMapper::toDomainModel);
    }

    @Override
    public Optional<Role> findRoleByNameWithTenantNull(String roleName) {
       Optional<RoleJpaEntity> roleJpaEntity = jpaRoleRepository.findRoleJpaEntitiesByNameAndTenantJpaEntity_IdNull(roleName);
        return roleJpaEntity.map(RoleJpaMapper::toDomainModel);
    }

    @Override
    public Optional<Role> findRoleByIdWithTenantNull(UUID roleId) {
        Optional<RoleJpaEntity> roleJpaEntity = jpaRoleRepository.findRoleJpaEntityByIdAndTenantJpaEntity_IdNull(roleId);

        return roleJpaEntity.map(RoleJpaMapper::toDomainModel);
    }

    @Override
    public Optional<Permission> findPermissionById(UUID permissionId) {
       Optional<PermissionJpaEntity> permissionJpaEntity = jpaPermissionRepository
               .findPermissionJpaEntityById(permissionId);

       return permissionJpaEntity.map(PermissionJpaMapper::toDomainModel);
    }

    @Override
    public Optional<Permission> findPermissionByCode(String code) {
        Optional<PermissionJpaEntity> permissionJpaEntity = jpaPermissionRepository.findPermissionJpaEntityByCode(code);

        return permissionJpaEntity.map(PermissionJpaMapper::toDomainModel);
    }

    @Override
    public List<Permission> findPermissionsByModuleKey(String moduleKey) {
        List<PermissionJpaEntity> permissionJpaEntityList = jpaPermissionRepository.findPermissionJpaEntityByModuleJpaEntity_ModuleKey(moduleKey);

        return permissionJpaEntityList.stream().map(
                PermissionJpaMapper::toDomainModel
        ).toList();
    }

    @Override
    @Transactional
    public Role saveRole(Role role) {
        RoleJpaEntity roleJpaEntity = jpaRoleRepository.save(
                RoleJpaMapper.toJpaEntity(role)
        );

        return RoleJpaMapper.toDomainModel(roleJpaEntity);
    }

    @Override
    @Transactional
    public void saveUserRole(UserRole userRole) {

        UserRoleJpaEntity userRoleJpaEntity =  jpaUserRoleRepository.save(UserRoleJpaMapper.toJpaEntity(userRole));

        UserRoleJpaMapper.toDomainModel(userRoleJpaEntity);
    }

    @Override
    @Transactional
    public Permission savePermission(Permission permission) {
        PermissionJpaEntity permissionJpaEntity = jpaPermissionRepository.save(
                PermissionJpaMapper.toJpaEntity(permission)
        );

        return PermissionJpaMapper.toDomainModel(permissionJpaEntity);
    }

    @Override
    @Transactional
    public void assignPermissionToRole(RolePermission rolePermission) {
        RolePermissionJpaEntity rolePermissionJpaEntity = jpaRolePermissionRepository.save(
                RolePermissionJpaMapper.toJpaEntity(rolePermission)
        );

        RolePermissionJpaMapper.toDomainModel(rolePermissionJpaEntity);
    }
}
