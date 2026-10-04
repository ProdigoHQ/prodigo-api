package com.licode.prodigoerp.auth.application.port.output;

import com.licode.prodigoerp.auth.domain.model.Permission;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PermissionPersistencePort {

    Permission save(Permission permission);

    Optional<Permission> findById(UUID id);
    Optional<Permission> findByCode(String code);
    Optional<Permission> findByCodeAndResource(String code, String resource);

    List<Permission> findAllByCodeIn(Collection<String> codes);
    List<Permission> findAllByCodeStartingWith(String prefix);
    List<Permission> findPermissionsByModuleKey(String moduleKey);
    List<String> findActivePermissionCodes(UUID userId);
    List<Permission> findAll();
    List<Permission> findAllTenantAssignable();
    List<Permission> findPermissionsByRoleId(UUID roleId);

    boolean existsByCode(String code);
    boolean isAssignedToAnyRole(UUID permissionId);

    void delete(Permission permission);
}
