package com.licode.prodigoerp.auth.adapter.output.persistence.Authority.mapper;

import com.licode.prodigoerp.auth.adapter.output.persistence.Authority.entity.PermissionJpaEntity;
import com.licode.prodigoerp.auth.domain.model.Permission;
import com.licode.prodigoerp.module.adapter.output.persistence.module.ModuleJpaMapper;

public class PermissionJpaMapper {

    public static PermissionJpaEntity toJpaEntity(Permission permission) {
        PermissionJpaEntity jpaEntity = new PermissionJpaEntity();

        jpaEntity.setId(permission.getId());
        jpaEntity.setDescription(permission.getDescription());
        jpaEntity.setCode(permission.getCode());
        jpaEntity.setAction(permission.getAction());
        jpaEntity.setResource(permission.getResource());
        jpaEntity.setModuleJpaEntity(permission.getModule() == null ? null : ModuleJpaMapper.toJpaEntity(permission.getModule()));
        jpaEntity.setTenantAssignable(permission.isTenantAssignable());
        jpaEntity.setCreatedAt(permission.getCreatedAt());
        jpaEntity.setUpdatedAt(permission.getUpdatedAt());
        jpaEntity.setCreatedBy(permission.getCreatedBy());
        jpaEntity.setUpdatedBy(permission.getUpdatedBy());

        return jpaEntity;
    }

    public static Permission toDomainModel(PermissionJpaEntity jpaEntity) {

        Permission permission = new Permission();

        permission.setId(jpaEntity.getId());
        permission.setDescription(jpaEntity.getDescription());
        permission.setCode(jpaEntity.getCode());
        permission.setAction(jpaEntity.getAction());
        permission.setResource(jpaEntity.getResource());
        permission.setModule(jpaEntity.getModuleJpaEntity() == null ? null : ModuleJpaMapper.toDomainModel(jpaEntity.getModuleJpaEntity()));
        permission.setTenantAssignable(jpaEntity.isTenantAssignable());
        permission.setCreatedAt(jpaEntity.getCreatedAt());
        permission.setUpdatedAt(jpaEntity.getUpdatedAt());
        permission.setCreatedBy(jpaEntity.getCreatedBy());
        permission.setUpdatedBy(jpaEntity.getUpdatedBy());

        return permission;
    }
}
