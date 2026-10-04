package com.licode.prodigoerp.auth.application.port.output;

import com.licode.prodigoerp.auth.domain.model.Permission;
import com.licode.prodigoerp.auth.domain.model.Role;
import com.licode.prodigoerp.auth.domain.model.RolePermission;
import com.licode.prodigoerp.auth.domain.model.UserRole;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RolePersistencePort {

    Role save(Role role);

    Optional<Role> findSystemRole(UUID roleId); // tenant IS NULL
    Optional<Role> findTenantRole(UUID roleId, UUID tenantId);

    List<Role> findSystemRoles();
    List<Role> findByTenantId(UUID tenantId);
    Optional<Role> findSystemRoleByName(String name);
    List<Permission> findPermissionsByRoleId(UUID roleId);
    List<String> findActiveRoleNames(UUID userId);

    boolean systemRoleNameExists(String name);
    boolean tenantRoleNameExists(String name, UUID tenantId);
    boolean isAssignedToUsers(UUID roleId);
    boolean rolePermissionExists(UUID roleId, UUID permissionId);
    boolean userRoleExists(UUID userId, UUID roleID, UUID tenantId);

    void saveUserRole(UserRole userRole);
    void delete(Role role);
    void saveRolePermission(RolePermission rolePermission);
    void deleteRolePermission(UUID roleId, UUID permissionId);
}
