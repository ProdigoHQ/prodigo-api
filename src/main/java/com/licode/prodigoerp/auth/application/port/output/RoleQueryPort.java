package com.licode.prodigoerp.auth.application.port.output;

import com.licode.prodigoerp.auth.domain.model.Permission;
import com.licode.prodigoerp.auth.domain.model.Role;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RoleQueryPort {
    // NOTE : WE will manage the query of the ROLE, USERROLE and PERMISSION here (all the queries)

    // Role Queries
    List<String> findActiveRoleNames(UUID userId);
    Optional<Role> findRoleByIdAndTenantId(UUID roleId, UUID tenantId);
    Optional<Role> findRoleByNameWithTenantNull(String roleName);
    Optional<Role> findRoleByIdWithTenantNull(UUID roleId);

    // Permission Queries
    List<String> findActivePermissionCodes(UUID userId);
    Optional<Permission> findPermissionById(UUID permissionId);
    Optional<Permission> findPermissionByCode(String code);
    List<Permission> findPermissionsByModuleKey(String moduleKey);
}
