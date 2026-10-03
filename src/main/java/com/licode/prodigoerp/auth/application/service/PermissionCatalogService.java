package com.licode.prodigoerp.auth.application.service;

import com.licode.prodigoerp.auth.application.helpers.AuthMappings;
import com.licode.prodigoerp.auth.application.port.input.PermissionCatalogUseCase;
import com.licode.prodigoerp.auth.application.port.input.command.CreatePermissionCommand;
import com.licode.prodigoerp.auth.application.port.input.command.PermissionSummaryCommand;
import com.licode.prodigoerp.auth.application.port.input.command.UpdatePermissionCommand;
import com.licode.prodigoerp.auth.application.port.output.PermissionPersistencePort;
import com.licode.prodigoerp.auth.domain.model.Permission;
import com.licode.prodigoerp.common.exception.ConflictException;
import com.licode.prodigoerp.common.exception.NotFoundException;
import com.licode.prodigoerp.common.shared.application.output.CurrentUserPort;
import com.licode.prodigoerp.module.application.port.input.ModuleLookUpUseCase;
import com.licode.prodigoerp.module.domain.model.Module;
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
public class PermissionCatalogService implements PermissionCatalogUseCase {

    private final PermissionPersistencePort permissionPort;
    private final ModuleLookUpUseCase moduleLookUp;
    private final CurrentUserPort currentUser;

    @Override
    public PermissionSummaryCommand create(CreatePermissionCommand cmd) {
        Module module = null;
        String code;

        if(cmd.moduleKey() == null) {
            // platform-only
            // platform permission samples: ERP.SYSTEM.ACCESS, ERP.SYSTEM.READ, ERP.CRM.UPDATE
            // ERP is the key word for al the super admin permission
            // System Permission format : ERP.Resouce.Action (Ex: READ, UPDATE...)
            code = "ERP." + cmd.resource() + "." + cmd.action();
        }else{
            module = moduleLookUp.findModuleByModuleKey(cmd.moduleKey().toUpperCase())
                    .orElseThrow(() -> new NotFoundException("Module with key " + cmd.moduleKey() + " not found"));

            // Permission sample : CRM.CUSTOMER.CREATE, CRM.MODULE.CRUD
            // Permission format : ModuleKey.Ressource.Action

            code = cmd.moduleKey() + "." + cmd.resource() + "." + cmd.action();
        }

        code = code.toUpperCase();

        if (permissionPort.existsByCode(code)) {
            throw new ConflictException("Permission with code " + code + " already exists");
        }

        String author = currentUser.username();
        Instant now = Instant.now();

        Permission p = new Permission();
        p.setId(null);
        p.setCode(code);
        p.setDescription(cmd.description());
        p.setAction(cmd.action().toUpperCase());
        p.setResource(cmd.resource().toUpperCase());
        p.setModule(module);
        p.setCreatedAt(now);
        p.setUpdatedAt(now);
        p.setCreatedBy(author);
        p.setUpdatedBy(author);

        log.info("Permission {} created by {}", code, author);

        return AuthMappings.toSummary(permissionPort.save(p));
    }

    @Override
    public PermissionSummaryCommand update(UUID permissionId, UpdatePermissionCommand command) {
        Permission p = load(permissionId);

        p.setDescription(command.description());
        p.setUpdatedAt(Instant.now());
        p.setUpdatedBy(currentUser.username());

        return AuthMappings.toSummary(permissionPort.save(p));
    }

    @Override
    @Transactional(readOnly = true)
    public PermissionSummaryCommand getPermissionById(UUID permissionId) {
        return AuthMappings.toSummary(load(permissionId));
    }

    @Override
    public void delete(UUID permissionId) {

        Permission p = load(permissionId);

        if(p.getModule() == null){
            throw new ConflictException("System permissions cannot be deleted");
        }

        if(permissionPort.isAssignedToAnyRole(permissionId)){
            throw new ConflictException("Permission {" + p.getCode() + "} is still assigned to one or more roles");
        }

        permissionPort.delete(p);
        log.info("Permission {} deleted by {}", permissionId, currentUser.username());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PermissionSummaryCommand> listPermissions() {

        return permissionPort.findAll().stream()
                .map(AuthMappings::toSummary).toList();
    }

    private Permission load(UUID permissionId) {
        return permissionPort.findById(permissionId)
                .orElseThrow(() -> new NotFoundException("Permission with id " + permissionId + " not found"));
    }
}
