package com.licode.prodigoerp.auth.application.service;

import com.licode.prodigoerp.auth.application.helpers.AuthMappings;
import com.licode.prodigoerp.auth.application.port.input.TenantRoleUseCase;
import com.licode.prodigoerp.auth.application.port.input.command.RoleSummaryCommand;
import com.licode.prodigoerp.auth.application.port.input.command.SaveRoleCommand;
import com.licode.prodigoerp.auth.application.port.output.RolePersistencePort;
import com.licode.prodigoerp.auth.domain.model.Role;
import com.licode.prodigoerp.common.exception.ConflictException;
import com.licode.prodigoerp.common.exception.NotFoundException;
import com.licode.prodigoerp.common.shared.application.output.CurrentUserPort;
import com.licode.prodigoerp.tenant.application.port.input.TenantLookUpUseCase;
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
public class TenantRoleService implements TenantRoleUseCase {

    private final RolePersistencePort rolePort;
    private final TenantLookUpUseCase tenantLookUp;
    private final CurrentUserPort currentUserPort;

    @Override
    public RoleSummaryCommand create(SaveRoleCommand command) {
        UUID tenantId = currentUserPort.requireTenantId();
        String name = command.name().trim();

        if (rolePort.tenantRoleNameExists(name, tenantId)) {
            throw new ConflictException("Role " + name + " already exists in your organization");
        }

        Instant now = Instant.now();
        String user = currentUserPort.username();

        Role role = new Role();
        role.setId(null);
        role.setName(name);
        role.setDescription(command.description());
        role.setTenant(tenantLookUp.findTenantById(tenantId));
        role.setIsDefault(false);
        role.setCreatedAt(now);
        role.setUpdatedAt(now);
        role.setCreatedBy(user);
        role.setUpdatedBy(user);

        return AuthMappings.toSummary(rolePort.save(role), List.of());
    }

    @Override
    public RoleSummaryCommand update(UUID roleId, SaveRoleCommand command) {
        UUID tenantId = currentUserPort.requireTenantId();
        Role role = load(roleId, tenantId);
        String name = command.name().trim();

        if(!role.getName().equalsIgnoreCase(name) && rolePort.tenantRoleNameExists(name, tenantId)) {
            throw new ConflictException("Role " + name + " already exists in your organization");
        }

        role.setName(name);
        role.setDescription(command.description());
        role.setUpdatedAt(Instant.now());
        role.setUpdatedBy(currentUserPort.username());

        Role saved = rolePort.save(role);

        return AuthMappings.toSummary(saved, rolePort.findPermissionsByRoleId(saved.getId()));
    }

    @Override
    @Transactional(readOnly = true)
    public RoleSummaryCommand get(UUID roleId) {
        Role role = load(roleId, currentUserPort.requireTenantId());

        return AuthMappings.toSummary(role, rolePort.findPermissionsByRoleId(roleId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<RoleSummaryCommand> list() {
        return rolePort.findByTenantId(currentUserPort.requireTenantId())
                .stream().map(r -> AuthMappings.toSummary(r, List.of()))
                .toList();
    }

    @Override
    public void delete(UUID roleId) {
        Role role = load(roleId, currentUserPort.requireTenantId());

        if(Boolean.TRUE.equals(role.getIsDefault())) {
            throw new ConflictException("Default roles cannot be deleted");
        }
        if(rolePort.isAssignedToUsers(roleId)) {
            throw new ConflictException("Role is still assigned to users");
        }

        rolePort.delete(role);
    }

    // helpers inner function
    private Role load(UUID roleId, UUID permissionId) {
        return rolePort.findTenantRole(roleId, permissionId)
                .orElseThrow(() -> new NotFoundException("Role not found with id: " + roleId));
    }
}
