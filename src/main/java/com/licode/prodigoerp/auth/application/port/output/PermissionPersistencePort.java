package com.licode.prodigoerp.auth.application.port.output;

import com.licode.prodigoerp.auth.domain.model.Permission;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PermissionPersistencePort {
    Permission save(Permission permission);
    Optional<Permission> findById(UUID id);
    boolean existsByCode(String code);
    List<Permission> findAll();
    List<Permission> findAllTenantAssignable();
    boolean isAssignedToAnyRole(UUID permissionId);
    void delete(Permission permission);
}
