package com.licode.prodigoerp.auth.application.service;

import com.licode.prodigoerp.auth.application.port.input.AuthoritiesUseCase;
import com.licode.prodigoerp.auth.application.port.input.command.AssignRoleCommand;
import com.licode.prodigoerp.auth.application.port.input.command.CreatePermissionCommand;
import com.licode.prodigoerp.auth.application.port.input.command.CreateRoleCommand;
import com.licode.prodigoerp.auth.application.port.input.command.PermissionSummaryCommand;
import com.licode.prodigoerp.auth.application.port.output.LoadUserPort;
import com.licode.prodigoerp.auth.application.port.output.RoleQueryPort;
import com.licode.prodigoerp.auth.application.port.output.SavePermissionPort;
import com.licode.prodigoerp.auth.application.port.output.SaveRolePort;
import com.licode.prodigoerp.auth.domain.model.*;
import com.licode.prodigoerp.common.exception.ConflictException;
import com.licode.prodigoerp.common.exception.NotFoundException;
import com.licode.prodigoerp.common.security.SecurityUtils;
import com.licode.prodigoerp.module.application.port.input.ModuleLookUpUseCase;
import com.licode.prodigoerp.module.domain.model.Module;
import com.licode.prodigoerp.tenant.application.port.input.TenantLookUpUseCase;
import com.licode.prodigoerp.tenant.domain.model.Tenant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthoritiesService implements AuthoritiesUseCase {

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
            log.error("User with id {} not found", assignRoleCommand.userId());
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
            log.error("Role not found with id: {}", assignRoleCommand.roleId());
            throw new NotFoundException("Role not found with id: " + assignRoleCommand.roleId());
        }

        Instant now = Instant.now();

        userRole.setRole(role.get());
        userRole.setUser(user.get());
        userRole.setTenantId(tenantId);
        userRole.setAssignedAt(now);
        userRole.setAssignedBy(assignRoleCommand.assignBy());
        userRole.setExpiresAt(now.plusSeconds(315576000)); // TODO (to be refactor) expires in 10 years

        log.info("Role with Id: {} assigned to User with Id: {}", assignRoleCommand.roleId(), assignRoleCommand.userId());
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
                log.error("Module not found with id: {}", permissionCommand.moduleKey());
                throw  new NotFoundException("Module with key " + permissionCommand.moduleKey() +" not found");
            }

            module = fetchedModule.get();
        }

        // check if there is no permission duplicate in the db
        // NOTE: permissionCode + resource should not be duplicate
        Optional<Permission> permissionExist = roleQueryPort.findPermissionsByCodeAndResource(permissionCode.toUpperCase(), permissionCommand.resource().toUpperCase());
        if (permissionExist.isPresent()) {
            log.error("Permission with code {} for the resource {} already exists", permissionCode.toUpperCase(),  permissionCommand.resource().toUpperCase());
            throw new ConflictException("Permission with code " + permissionCode.toUpperCase() + " already exists. Please choose another one");
        }

        permission.setId(null);
        permission.setCode(permissionCode.toUpperCase());
        permission.setDescription(permissionCommand.description());
        permission.setAction(permissionCommand.action().toUpperCase());
        permission.setResource(permissionCommand.resource().toUpperCase());
        permission.setModule(module);

        permission.setCreatedAt(now);
        permission.setUpdatedAt(now);
        permission.setCreatedBy(author);
        permission.setUpdatedBy(author);

        log.info("Permission with code: {} created on: {}", permissionCode, now);

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
            log.error("Role not found with id: {}", assignRoleCommand.roleId());
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

    @Override
    public PermissionSummaryCommand fetchPermissionSummary(UUID permissionId) {

        Optional<Permission> permission = roleQueryPort.findPermissionById(permissionId);

        if(permission.isEmpty()) {
            log.error("Permission not found with id: {}", permissionId);
            throw new NotFoundException("Permission not found with id: " + permissionId);
        }

        return new PermissionSummaryCommand(
                permission.get().getId(),
                permission.get().getCode(),
                permission.get().getDescription(),
                permission.get().getAction(),
                permission.get().getResource()
        );
    }

    @Override
    public void deletePermission(UUID permissionId) {
        roleQueryPort.deletePermissionById(permissionId);
        String currentUserLogin = SecurityUtils.getCurrentUser().username();
        log.info("Permission with id: {} has been deleted by {}", permissionId, currentUserLogin);
    }


}
