package com.licode.prodigoerp.auth.adapter.output.persistence.Authority.repository;

import com.licode.prodigoerp.auth.adapter.output.persistence.Authority.entity.PermissionJpaEntity;
import com.licode.prodigoerp.auth.adapter.output.persistence.Authority.entity.RolePermissionJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface JpaRolePermissionRepository extends JpaRepository<RolePermissionJpaEntity, UUID> {

//    SELECT p.* FROM role_permissions rp INNER JOIN permissions p ON rp.permission_id = p.id WHERE rp.role_id = 'fb8fb969-eab2-4564-8b69-c11f6a07de91';
    @Query("SELECT p FROM RolePermissionJpaEntity rp INNER JOIN PermissionJpaEntity p ON rp.permissionJpaEntity.id = p.id WHERE rp.roleJpaEntity.id = :roleJpaEntityId")
    List<PermissionJpaEntity> findRolePermissionJpaEntitiesByRoleJpaEntity_Id(UUID roleJpaEntityId);

    boolean existsByPermissionJpaEntity_Id(UUID permissionJpaEntityId);

    @Modifying
    @Query("DELETE FROM RolePermissionJpaEntity  rp WHERE rp.roleJpaEntity.id = :roleId and rp.permissionJpaEntity.id = :permissionId")
    int deleteByRoleIdAndPermissionId(@Param("roleId") UUID roleId, @Param("permissionId") UUID permissionId);

    @Modifying
    @Query("delete from RolePermissionJpaEntity rp where rp.roleJpaEntity.id = :roleId")
    int deleteAllByRoleId(@Param("roleId") UUID roleId);
}
