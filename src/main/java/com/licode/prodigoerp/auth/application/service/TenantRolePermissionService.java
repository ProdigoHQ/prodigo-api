package com.licode.prodigoerp.auth.application.service;

import com.licode.prodigoerp.auth.application.helpers.AuthMappings;
import com.licode.prodigoerp.auth.application.helpers.RolePermissionLinker;
import com.licode.prodigoerp.auth.application.port.input.TenantRolePermissionUseCase;
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
public class TenantRolePermissionService implements TenantRolePermissionUseCase {

    private final RolePersistencePort rolePort;
    private final PermissionPersistencePort permissionPort;
    private final RolePermissionLinker linker;
    private final CurrentUserPort currentUserPort;

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
        Role role = loadRole(roleId);
        Permission permission = loadAssignable(permissionId);

        // TODO: anti-escalation: you can only grant what you hold yourself

        linker.link(role, permission, currentUserPort.username());
        log.info("Permission {} assigned to role {} by {}", permission.getCode(), roleId, currentUserPort.username());
    }

    @Override
    public void remove(UUID roleId, UUID permissionId) {
        linker.unlink(loadRole(roleId), loadAssignable(permissionId));
    }

    // helpers functions

    private Role loadRole(UUID roleId) {
        return rolePort.findTenantRole(roleId, currentUserPort.requireTenantId())
                .orElseThrow(() -> new NotFoundException("Role not found with id: " + roleId));
    }

    private Permission loadAssignable(UUID id) {
        return permissionPort.findById(id)
                .filter(Permission::isTenantAssignable)
                .orElseThrow(() -> new NotFoundException("Permission not found with id: " + id));
    }
}
