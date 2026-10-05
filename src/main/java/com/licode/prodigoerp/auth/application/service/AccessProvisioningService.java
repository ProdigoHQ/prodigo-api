package com.licode.prodigoerp.auth.application.service;

import com.licode.prodigoerp.auth.application.helpers.RolePermissionLinker;
import com.licode.prodigoerp.auth.application.port.input.command.EnsurePermissionCommand;
import com.licode.prodigoerp.auth.application.port.input.internal.AccessProvisioningUseCase;
import com.licode.prodigoerp.auth.application.port.output.LoadUserPort;
import com.licode.prodigoerp.auth.application.port.output.PermissionPersistencePort;
import com.licode.prodigoerp.auth.application.port.output.RolePersistencePort;
import com.licode.prodigoerp.auth.domain.model.Permission;
import com.licode.prodigoerp.auth.domain.model.Role;
import com.licode.prodigoerp.auth.domain.model.User;
import com.licode.prodigoerp.auth.domain.model.UserRole;
import com.licode.prodigoerp.common.exception.NotFoundException;
import com.licode.prodigoerp.module.application.port.input.ModuleLookUpUseCase;
import com.licode.prodigoerp.module.domain.model.Module;
import com.licode.prodigoerp.tenant.application.port.input.TenantLookUpUseCase;
import com.licode.prodigoerp.tenant.domain.model.Tenant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class AccessProvisioningService implements AccessProvisioningUseCase {

    private static final String SUPER_ADMIN_ROLE = "SUPER_ADMIN";
    private static final String TENANT_ADMIN_ROLE = "ADMIN";

    private final RolePersistencePort rolePort;
    private final PermissionPersistencePort permissionPort;
    private final ModuleLookUpUseCase moduleLookUp;
    private final TenantLookUpUseCase tenantLookUp;
    private final LoadUserPort loadUserPort;
    private final RolePermissionLinker linker;

    @Override
    public UUID ensureSuperAdminRole(String author) {
        Role role = rolePort.findSystemRoleByName(SUPER_ADMIN_ROLE)
                .orElseGet(() -> rolePort.save(newRole(
                        SUPER_ADMIN_ROLE, null,
                        "SUPER_ADMIN: default role to access the ERP system dashboard",
                        author
                )));

        List<Permission> platformPermissions = permissionPort.findAllByCodeStartingWith("ERP.");

        if(platformPermissions.isEmpty()) {
            throw new IllegalStateException("No ERP.* permissions found. Please contact SUPER ADMIN.");
        }

        for(Permission p : platformPermissions){
            if(!rolePort.rolePermissionExists(role.getId(), p.getId())){
                linker.link(role, p, author);
            }
        }

        return role.getId();
    }

    @Override
    public UUID createTenantAdminRole(UUID tenantId, Collection<String> moduleKeys, String author) {

        Set<String> codes = moduleKeys.stream()
                .map(k -> (k + ".MODULE.CRUD").toUpperCase())
                .collect(Collectors.toSet());

        List<Permission> permissions = permissionPort.findAllByCodeIn(codes);
        if(permissions.size() != codes.size()) {
            throw new IllegalStateException("Missing module permissions: " + codes);
        }

        Tenant tenant = tenantLookUp.findTenantById(tenantId);

        Role role = rolePort.save(newRole(
                TENANT_ADMIN_ROLE,
                tenant,
                "ADMIN: full access to the tenant (company)",
                author
        ));

        permissions.forEach(p -> linker.link(role, p, author));
        log.info("Admin role {} created for tenant {} with {}", role.getId(), tenantId, codes);

        return role.getId();
    }

    @Override
    public void assignRoleToUser(UUID userId, UUID roleId, UUID tenantId, String author) {

        if (rolePort.userRoleExists(userId, roleId, tenantId)) {
            return;
        }

        User user = loadUserPort.findUserById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + userId));

        Optional<Role> role = (tenantId == null)
                ? rolePort.findSystemRole(roleId)
                : rolePort.findTenantRole(roleId, tenantId);

        Role found = role.orElseThrow(() -> new NotFoundException("Role not found with id: " + roleId));

        UserRole ur = new UserRole();
        ur.setId(null);
        ur.setUser(user);
        ur.setRole(found);
        ur.setTenantId(tenantId);
        ur.setAssignedAt(Instant.now());
        ur.setAssignedBy(author);
        ur.setExpiresAt(null);
        rolePort.saveUserRole(ur);

        log.info("Role {} assigned to user {} by {}", roleId, userId, author);
    }

    @Override
    public String ensurePermission(EnsurePermissionCommand cmd) {

        Module module = null;
        String code;

        if (cmd.moduleKey() == null) {
            code = "ERP." + cmd.resource() + "." + cmd.action();
        } else {
            module = moduleLookUp.findModuleByModuleKey(cmd.moduleKey())
                    .orElseThrow(() -> new IllegalStateException("Unknown module " + cmd.moduleKey()));

            code = cmd.moduleKey() + "." + cmd.resource() + "." + cmd.action();
        }
        code = code.toUpperCase();

        if (!permissionPort.existsByCode(code)) {

            Instant now = Instant.now();
            Permission p = new Permission();
            p.setId(null);
            p.setCode(code);
            p.setDescription(cmd.description());
            p.setAction(cmd.action().toUpperCase());
            p.setResource(cmd.resource().toUpperCase());
            p.setModule(module);
            p.setTenantAssignable(module != null);                 // ERP.* stays platform-only
            p.setCreatedAt(now);
            p.setUpdatedAt(now);
            p.setCreatedBy("SYSTEM");
            p.setUpdatedBy("SYSTEM");
            permissionPort.save(p);

            log.info("Permission {} seeded", code);
        }
        return code;
    }

    // helper functions

    private Role newRole(String name, Tenant tenant, String description, String author){
        Instant now = Instant.now();

        Role role = new Role();

        role.setId(null);
        role.setName(name);
        role.setDescription(description);
        role.setTenant(tenant);
        role.setIsDefault(true);
        role.setCreatedAt(now);
        role.setUpdatedAt(now);
        role.setCreatedBy(author);
        role.setUpdatedBy(author);

        return role;
    }
}
