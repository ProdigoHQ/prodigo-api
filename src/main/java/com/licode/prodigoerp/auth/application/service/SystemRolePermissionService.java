package com.licode.prodigoerp.auth.application.service;

import com.licode.prodigoerp.auth.application.helpers.AuthMappings;
import com.licode.prodigoerp.auth.application.helpers.RolePermissionLinker;
import com.licode.prodigoerp.auth.application.port.input.SystemRolePermissionUseCase;
import com.licode.prodigoerp.auth.application.port.input.command.PermissionSummaryCommand;
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

import java.util.List;
import java.util.UUID;

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
    public List<PermissionSummaryCommand> list(UUID roleId) {
        loadRole(roleId);

        return rolePort.findPermissionsByRoleId(roleId)
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
