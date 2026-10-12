package com.licode.prodigoerp.auth.application.port.input.internal;

import com.licode.prodigoerp.auth.application.port.input.command.EnsurePermissionCommand;

import java.util.Collection;
import java.util.UUID;

// internal usage. Never called from a controller
public interface AccessProvisioningUseCase {

    // Creates the SUPER_ADMIN role if missing and grants it every ERP.* permission.
    UUID ensureSuperAdminRole(String author);

    // Creates the ADMIN role for a new tenant with KEY.MODULE.CRUD for each subscribed module.
    UUID createTenantAdminRole(UUID tenantId, Collection<String> moduleKeys, String author);

    // Creates the OWNER role for a new tenant with KEY.MODULE.CRUD for each subscribed module.
    UUID createTenantOwnerRole(UUID tenantId, Collection<String> moduleKeys, String author);

    // Creates the default member role for a new tenant.
    UUID createTenantMemberRole(UUID tenantId, String author);

    void assignRoleToUser(UUID userID, UUID roleId, UUID tenantId, String author);

    // creates the permission if missing, returns its code. NOTE: will be used by seeders
    String ensurePermission(EnsurePermissionCommand command);
}
