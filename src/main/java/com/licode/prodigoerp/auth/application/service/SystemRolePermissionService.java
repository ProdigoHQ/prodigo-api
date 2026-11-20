package com.licode.prodigoerp.auth.application.service;

import com.licode.prodigoerp.auth.application.helpers.AuthMappings;
import com.licode.prodigoerp.auth.application.helpers.RolePermissionLinker;
import com.licode.prodigoerp.auth.application.port.input.SystemRolePermissionUseCase;
import com.licode.prodigoerp.auth.application.port.input.command.AssignSysPermissionsRoleCommand;
import com.licode.prodigoerp.auth.application.port.input.query.ListRolePermissionsQuery;
import com.licode.prodigoerp.auth.application.port.input.result.PermissionSummaryResult;
import com.licode.prodigoerp.auth.application.port.output.PermissionPersistencePort;
import com.licode.prodigoerp.auth.application.port.output.RolePersistencePort;
import com.licode.prodigoerp.auth.domain.model.Permission;
import com.licode.prodigoerp.auth.domain.model.Role;
import com.licode.prodigoerp.common.exception.NotFoundException;
import com.licode.prodigoerp.common.shared.application.output.CurrentUserPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class SystemRolePermissionService implements SystemRolePermissionUseCase {

    private final RolePersistencePort rolePort;
    private final PermissionPersistencePort permissionPort;
    private final RolePermissionLinker linker;
    private final CurrentUserPort currentUser;

    @Override
    @Transactional(readOnly = true)
    public List<PermissionSummaryResult> list(ListRolePermissionsQuery query) {
        loadRole(query.roleId());

        return rolePort.findPermissionsByRoleId(query.roleId())
                .stream().map(AuthMappings::toSummary)
                .toList();
    }

    @Override
    public void assign(UUID roleId, UUID permissionId) {
        linker.link(
                loadRole(roleId),
                loadPermission(permissionId),
                currentUser.username()
        );
    }

    @Override
    public void remove(UUID roleId, UUID permissionId) {
        linker.unlink(loadRole(roleId), loadPermission(permissionId));
    }

    @Override
    public String sync(UUID roleId,  AssignSysPermissionsRoleCommand command) {
        Role role = loadRole(roleId);

        Set<UUID> requestedIds = new HashSet<>(command.permissionIds());

        List<Permission> currentPermissions = rolePort.findPermissionsByRoleId(roleId);

        Map<UUID, Permission> currentPermissionsById = currentPermissions.stream()
                .collect(Collectors.toMap(Permission::getId, Function.identity()));

        Set<UUID> currentPermissionIds = currentPermissionsById.keySet();

        Set<UUID> toAssignIds = new LinkedHashSet<>(requestedIds);
        toAssignIds.removeAll(currentPermissionIds);

        Set<UUID> toRevokeIds = new LinkedHashSet<>(currentPermissionIds);
        toRevokeIds.removeAll(requestedIds);

        List<Permission> permissionsToAssign =
                permissionPort.findAllByIds(toAssignIds);

        // Validate that every requested permission exists.
        Set<UUID> foundIds = permissionsToAssign.stream()
                .map(Permission::getId)
                .collect(Collectors.toSet());

        if (!foundIds.containsAll(toAssignIds)) {
            Set<UUID> missingIds = new HashSet<>(toAssignIds);
            missingIds.removeAll(foundIds);

            throw new NotFoundException(
                    "Permissions not found: " + missingIds
            );
        }

        // Apply additions.
        String actor = currentUser.usernameOrSystem();

        for (Permission permission : permissionsToAssign) {
            linker.link(role, permission, actor);
        }

        // Apply revocations.
        for (UUID permissionId : toRevokeIds) {
            linker.unlink(role, currentPermissionsById.get(permissionId));
        }

        log.info("The Role: {}'s permissions was modified by {}", roleId, actor);
        return "Role's permissions was modified by successfully";
    }

    // private helper functions

    private Role loadRole(UUID roleId) {
        return rolePort.findSystemRole(roleId)
                .orElseThrow(() -> new NotFoundException("Role not found with id: " + roleId));
    }

    private Permission loadPermission(UUID permissionId) {
        return permissionPort.findById(permissionId)
                .orElseThrow(() -> new NotFoundException("Permission not found with id: " + permissionId));
    }
}
