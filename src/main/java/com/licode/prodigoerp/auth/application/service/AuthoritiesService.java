package com.licode.prodigoerp.auth.application.service;

import com.licode.prodigoerp.auth.application.port.input.SaveAuthoritiesUseCase;
import com.licode.prodigoerp.auth.application.port.input.command.AssignRoleCommand;
import com.licode.prodigoerp.auth.application.port.input.command.CreatePermissionCommand;
import com.licode.prodigoerp.auth.application.port.input.command.CreateRoleCommand;
import com.licode.prodigoerp.auth.application.port.output.LoadUserPort;
import com.licode.prodigoerp.auth.application.port.output.RoleQueryPort;
import com.licode.prodigoerp.auth.application.port.output.SavePermissionPort;
import com.licode.prodigoerp.auth.application.port.output.SaveRolePort;
import com.licode.prodigoerp.auth.domain.model.*;
import com.licode.prodigoerp.common.exception.NotFoundException;
import com.licode.prodigoerp.module.application.port.input.ModuleLookUpUseCase;
import com.licode.prodigoerp.module.domain.model.Module;
import com.licode.prodigoerp.tenant.application.port.input.TenantLookUpUseCase;
import com.licode.prodigoerp.tenant.domain.model.Tenant;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthoritiesService implements SaveAuthoritiesUseCase {

    private final SaveRolePort saveRolePort;
    private final SavePermissionPort savePermissionPort;
    private final ModuleLookUpUseCase moduleLookUpUseCase;
    private final TenantLookUpUseCase tenantLookUpUseCase;
    private final LoadUserPort loadUserPort;
    private final RoleQueryPort roleQueryPort;

    @Override
    @Transactional
    public Role saveRole(CreateRoleCommand roleCommand) {

        Role role = new Role();

        Instant now = Instant.now();

        role.setId(null);
        role.setName(roleCommand.roleName());
        role.setDescription(roleCommand.description());
        role.setTenant(roleCommand.tenant());
        role.setIsDefault(roleCommand.isDefault());

        role.setCreatedAt(now);
        role.setUpdatedAt(now);
        role.setCreatedBy(roleCommand.author());
        role.setUpdatedBy(roleCommand.author());

        return  saveRolePort.saveRole(role);
    }

    @Override
    @Transactional
    public void assignedRoleToUser(AssignRoleCommand assignRoleCommand) {

        UserRole userRole = new UserRole();

        userRole.setId(null);
        Optional<Role> role;
        Tenant tenant;
        UUID tenantId;

        Optional<User> user = loadUserPort.findUserById(assignRoleCommand.userId());

        if (user.isEmpty()) {
            throw new NotFoundException("User not found with id: " + assignRoleCommand.userId());
        }

        if(assignRoleCommand.tenantId() == null){
            tenantId = null;
            role = roleQueryPort.findRoleByIdWithTenantNull(assignRoleCommand.roleId());
        }else {
            tenant = tenantLookUpUseCase.findTenantById(assignRoleCommand.tenantId());
            tenantId = tenant.getId();
            role = roleQueryPort.findRoleByIdAndTenantId(assignRoleCommand.roleId(), tenant.getId());
        }

        if (role.isEmpty()) {
            throw new NotFoundException("Role not found with id: " + assignRoleCommand.roleId());
        }

        Instant now = Instant.now();

        userRole.setRole(role.get());
        userRole.setUser(user.get());
        userRole.setTenantId(tenantId);
        userRole.setAssignedAt(now);
        userRole.setAssignedBy(assignRoleCommand.assignBy());
        userRole.setExpiresAt(now.plusSeconds(315576000)); // TODO (to be refactor) expires in 10 years

        saveRolePort.saveUserRole(userRole);
    }

    @Override
    @Transactional
    public Permission savePermission(CreatePermissionCommand permissionCommand, String author) {
        Permission permission = new Permission();
        Instant now = Instant.now();
        Module module;
        String permissionCode;


        // moduleKey can be null since the System permission is not associated to any module
        // it can happen that a super admin permission are associated just to a module
        // meaning the super admin is just responsible for that specific module management
        if(permissionCommand.moduleKey() == null){
            // system permission samples: ERP.SYSTEM.ACCESS, ERP.SYSTEM.READ, ERP.CRM.UPDATE
            // ERP is the key word for al the super admin permission
            // System Permission format : ERP.Resouce.Action (Ex: READ, UPDATE...)
            permissionCode = "ERP" + "." + permissionCommand.resource() + "." + permissionCommand.action();

            module = null;

        }else{
            // Permission sample : CRM.CUSTOMER.CREATE, CRM.MODULE.CRUD
            // Permission format : ModuleKey.Ressource.Action
            permissionCode = permissionCommand.moduleKey() + "." + permissionCommand.resource() + "." + permissionCommand.action();

            // We got the module key, then we need to fetch the whole Module object to create the associate permission
            Optional<Module> fetchedModule = moduleLookUpUseCase.findModuleByModuleKey(permissionCommand.moduleKey());

            if (fetchedModule.isEmpty()) {
                throw  new NotFoundException("Module with key " + permissionCommand.moduleKey() +" not found - Permission create");
            }

            module = fetchedModule.get();
        }

        permission.setId(null);
        permission.setCode(permissionCode);
        permission.setDescription(permissionCommand.description());
        permission.setAction(permissionCommand.action());
        permission.setResource(permissionCommand.resource());
        permission.setModule(module);

        permission.setCreatedAt(now);
        permission.setUpdatedAt(now);
        permission.setCreatedBy(author);
        permission.setUpdatedBy(author);

        return savePermissionPort.savePermission(permission);
    }

    @Override
    @Transactional
    public void assignedPermissionToRole(UUID permissionId, AssignRoleCommand assignRoleCommand) {

        RolePermission rolePermission = new RolePermission();
        rolePermission.setId(null);

        Optional<Role> role;

        if(assignRoleCommand.tenantId() == null){
            role = roleQueryPort.findRoleByIdAndTenantId(assignRoleCommand.roleId(), null);
        }else{
            role = roleQueryPort.findRoleByIdAndTenantId(assignRoleCommand.roleId(), assignRoleCommand.tenantId());
        }

        if (role.isEmpty()) {
            throw new NotFoundException("Role not found with id: " + assignRoleCommand.roleId());
        }

        Optional<Permission> permission = roleQueryPort.findPermissionById(permissionId);

        if (permission.isEmpty()) {
            throw new NotFoundException("Permission not found with id: " + permissionId);
        }

        rolePermission.setPermission(permission.get());
        rolePermission.setRole(role.get());
        rolePermission.setGrantedAt(Instant.now());
        rolePermission.setGrantedBy(assignRoleCommand.assignBy());

        savePermissionPort.assignPermissionToRole(rolePermission);
    }


}
