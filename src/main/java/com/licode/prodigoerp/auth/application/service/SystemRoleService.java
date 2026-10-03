package com.licode.prodigoerp.auth.application.service;

import com.licode.prodigoerp.auth.application.helpers.AuthMappings;
import com.licode.prodigoerp.auth.application.port.input.SystemRoleUseCase;
import com.licode.prodigoerp.auth.application.port.input.command.RoleSummaryCommand;
import com.licode.prodigoerp.auth.application.port.input.command.SaveRoleCommand;
import com.licode.prodigoerp.auth.application.port.output.RolePersistencePort;
import com.licode.prodigoerp.auth.domain.model.Role;
import com.licode.prodigoerp.common.exception.ConflictException;
import com.licode.prodigoerp.common.exception.NotFoundException;
import com.licode.prodigoerp.common.shared.application.output.CurrentUserPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class SystemRoleService implements SystemRoleUseCase {

    private final CurrentUserPort  currentUser;
    private final RolePersistencePort rolePort;

    @Override
    public RoleSummaryCommand create(SaveRoleCommand cmd) {
        String name = cmd.name().trim();
        if(rolePort.systemRoleNameExists(name)) {
            throw new ConflictException("System role " + name + " already exists");
        }
        Role role = new Role();
        role.setId(null);
        role.setName(name);
        role.setDescription(cmd.description());
        role.setTenant(null);
        role.setIsDefault(false);

        stamp(role, true);
        return AuthMappings.toSummary(rolePort.save(role), List.of());
    }

    @Override
    public RoleSummaryCommand update(UUID roleId, SaveRoleCommand command) {
        Role role = loadSystemRole(roleId);

        String name = command.name().trim();
        if(!role.getName().equalsIgnoreCase(name) && rolePort.systemRoleNameExists(name)) {
            throw new ConflictException("System role " + name + " already exists");
        }

        role.setName(name);
        role.setDescription(command.description());

        stamp(role, false);

        Role saved = rolePort.save(role);

        return AuthMappings.toSummary(saved, rolePort.findPermissionsByRoleId(saved.getId()));
    }

    @Override
    @Transactional(readOnly = true)
    public RoleSummaryCommand get(UUID roleId) {
        Role role = loadSystemRole(roleId);

        return AuthMappings.toSummary(role, rolePort.findPermissionsByRoleId(roleId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<RoleSummaryCommand> list() {
        return rolePort.findSystemRoles().stream()
                .map(r -> AuthMappings.toSummary(r, List.of()))
                .toList();
    }

    @Override
    public void delete(UUID roleId) {
        Role role = loadSystemRole(roleId);

        if(rolePort.isAssignedToUsers(roleId)) {
            throw new ConflictException("Role " + roleId + " already assigned to users");
        }

        rolePort.delete(role);
        log.info("System role {} deleted by {}", roleId, currentUser.username());
    }

    @Override
    @Transactional(readOnly = true)
    public List<RoleSummaryCommand> listTenantRoles(UUID tenantId) {
        return rolePort.findByTenantId(tenantId).stream()
                .map(r -> AuthMappings.toSummary(r, List.of()))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public RoleSummaryCommand getTenantRole(UUID tenantId, UUID roleId) {
        Role role = rolePort.findTenantRole(roleId, tenantId)
                .orElseThrow(() -> new NotFoundException("Role not found with id: " + roleId));

        return AuthMappings.toSummary(role, rolePort.findPermissionsByRoleId(roleId));
    }
    
    // On creating set all fields else just set the updated fields
    private void stamp(Role role, boolean creating) {
        Instant now = Instant.now();
        String user = currentUser.username();

        if(creating) {
            role.setCreatedAt(now);
            role.setCreatedBy(user);
        }

        role.setUpdatedAt(now);
        role.setUpdatedBy(user);
    }

    private Role loadSystemRole(UUID roleId) {
        return rolePort.findSystemRole(roleId)
                .orElseThrow(() -> new NotFoundException("Role not found with id: " + roleId));
    }
}
