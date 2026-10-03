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
import com.licode.prodigoerp.auth.application.port.output.RolePersistencePort;
import com.licode.prodigoerp.auth.application.port.output.RoleQueryPort;
import com.licode.prodigoerp.auth.application.port.output.SavePermissionPort;
import com.licode.prodigoerp.auth.application.port.output.SaveRolePort;
import com.licode.prodigoerp.auth.domain.model.Permission;
import com.licode.prodigoerp.auth.domain.model.Role;
import com.licode.prodigoerp.auth.domain.model.RolePermission;
import com.licode.prodigoerp.auth.domain.model.UserRole;
import com.licode.prodigoerp.common.exception.ConflictException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class RolePersistenceAdapter implements RolePersistencePort, RoleQueryPort, SaveRolePort, SavePermissionPort {
    final private JpaRoleRepository jpaRoleRepository;
    final private JpaUserRoleRepository jpaUserRoleRepository;
    final private JpaPermissionRepository jpaPermissionRepository;
    final private JpaRolePermissionRepository jpaRolePermissionRepository;

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
    /// TODO: Refactor the above
    ///

    @Override
    public List<String> findActiveRoleNames(UUID userId) {

        return jpaUserRoleRepository.findActiveRoleNamesByUserId(userId);
    }


    @Override
    public Role save(Role role) {
        RoleJpaEntity roleJpaEntity = jpaRoleRepository.save(RoleJpaMapper.toJpaEntity(role));
        return RoleJpaMapper.toDomainModel(roleJpaEntity);
    }

    @Override
    public Optional<Role> findSystemRole(UUID roleId) {
        return jpaRoleRepository.findRoleJpaEntityByIdAndTenantJpaEntity_IdNull(roleId)
                .map(RoleJpaMapper::toDomainModel);
    }

    @Override
    public Optional<Role> findTenantRole(UUID roleId, UUID tenantId) {
        return jpaRoleRepository.findRoleJpaEntityByIdAndTenantJpaEntity_Id(roleId, tenantId)
                .map(RoleJpaMapper::toDomainModel);
    }

    @Override
    public List<Role> findSystemRoles() {
        return jpaRoleRepository.findAllByTenantJpaEntityNullOrderByNameAsc()
                .stream().map(RoleJpaMapper::toDomainModel)
                .toList();
    }

    @Override
    public List<Role> findByTenantId(UUID tenantId) {
        return jpaRoleRepository.findAllByTenantJpaEntity_IdOrderByNameAsc(tenantId)
                .stream().map(RoleJpaMapper::toDomainModel)
                .toList();
    }

    @Override
    public List<Permission> findPermissionsByRoleId(UUID roleId) {
        return jpaRolePermissionRepository.findRolePermissionJpaEntitiesByRoleJpaEntity_Id(roleId)
                .stream().map(PermissionJpaMapper::toDomainModel)
                .toList();
    }

    @Override
    public boolean systemRoleNameExists(String name) {
        return jpaRoleRepository.existsByNameIgnoreCaseAndTenantJpaEntityNull(name);
    }

    @Override
    public boolean tenantRoleNameExists(String name, UUID tenantId) {
        return jpaRoleRepository.existsByNameIgnoreCaseAndTenantJpaEntity_Id(name,tenantId);
    }

    @Override
    public boolean isAssignedToUsers(UUID roleId) {
        return jpaUserRoleRepository.existsByRoleJpaEntity_Id(roleId);
    }

    @Override
    public boolean rolePermissionExists(UUID roleId, UUID permissionId) {
        return jpaRolePermissionRepository.existsByRoleJpaEntity_IdAndPermissionJpaEntity_Id(roleId, permissionId);
    }

    @Override
    public void delete(Role role) {
        // remove the join rows first so the FK doesn't block the delete
        jpaRolePermissionRepository.deleteAllByRoleId(role.getId());

        jpaRoleRepository.delete(RoleJpaMapper.toJpaEntity(role));
    }

    @Override
    public void saveRolePermission(RolePermission rolePermission) {
        try{
            jpaRolePermissionRepository.save(RolePermissionJpaMapper.toJpaEntity(rolePermission));
        } catch (DataIntegrityViolationException e) {
            throw new ConflictException("Permission is already assigned to this role");
        }
    }

    @Override
    public void deleteRolePermission(UUID roleId, UUID permissionId) {
        jpaRolePermissionRepository.deleteByRoleIdAndPermissionId(roleId, permissionId);
    }
}
