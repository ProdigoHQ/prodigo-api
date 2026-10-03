package com.licode.prodigoerp.auth.adapter.output.persistence.Authority.repository;

import com.licode.prodigoerp.auth.adapter.output.persistence.Authority.entity.UserRoleJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface JpaUserRoleRepository extends JpaRepository<UserRoleJpaEntity, UUID> {

    @Query("""
        SELECT r.name FROM UserRoleJpaEntity ur JOIN ur.roleJpaEntity r
        WHERE ur.userJpaEntity.id = :userId
        AND (ur.expiresAt IS NULL OR ur.expiresAt > CURRENT_TIMESTAMP)
        """)
    List<String> findActiveRoleNamesByUserId(@Param("userId") UUID userId);

    @Query("""
        SELECT DISTINCT p.code FROM UserRoleJpaEntity ur
        JOIN ur.roleJpaEntity r
        JOIN RolePermissionJpaEntity rp ON rp.roleJpaEntity= r
        JOIN rp.permissionJpaEntity p
        WHERE ur.userJpaEntity.id = :userId
        AND (ur.expiresAt IS NULL OR ur.expiresAt > CURRENT_TIMESTAMP)
        """)
    List<String> findActivePermissionCodesByUserId(@Param("userId") UUID userId);

    boolean existsByRoleJpaEntity_Id(UUID roleId);
}
