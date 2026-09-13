package com.licode.prodigoerp.tenant.application.service;

import com.licode.prodigoerp.auth.application.port.input.command.PermissionSummaryCommand;
import com.licode.prodigoerp.auth.application.port.output.RoleQueryPort;
import com.licode.prodigoerp.auth.domain.model.Permission;
import com.licode.prodigoerp.module.application.port.input.command.ModuleSummaryCommand;
import com.licode.prodigoerp.tenant.application.port.input.TenantModuleUseCase;
import com.licode.prodigoerp.module.application.port.input.command.ShowPublicModuleCommand;
import com.licode.prodigoerp.tenant.application.port.output.TenantModuleQueryPort;
import com.licode.prodigoerp.module.domain.model.Module;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TenantModuleService implements TenantModuleUseCase {

    private final TenantModuleQueryPort tenantModuleQueryPort;
    private final RoleQueryPort roleQueryPort;

    @Override
    public List<ShowPublicModuleCommand> findAllActiveModulesByTenantId(UUID tenantId) {
        List<Module> modules = tenantModuleQueryPort.findAllActiveModulesByTenantId(tenantId);

        return modules.stream().map(
                module -> new ShowPublicModuleCommand(
                        module.getId(),
                        module.getName(),
                        module.getDescription(),
                        module.getModuleKey(),
                        module.getPrice(),
                        module.getCurrency()
                )
        ).toList();
    }

    @Override
    public List<ShowPublicModuleCommand> findAllAvailableModulesToPay(UUID tenantId) {

        List<Module> modules = tenantModuleQueryPort.findAllAvailableModulesToPay(tenantId);

        return modules.stream().map(
                module -> new ShowPublicModuleCommand(
                        module.getId(),
                        module.getName(),
                        module.getDescription(),
                        module.getModuleKey(),
                        module.getPrice(),
                        module.getCurrency()
                )
        ).toList();
    }

    @Override
    public ModuleSummaryCommand findModuleWithPermissionsByKeyAndTenantId(String moduleKey, UUID tenantId) {

        Module fetchedModule = tenantModuleQueryPort.findModuleByModuleKeyAndTenantId(moduleKey.toUpperCase(), tenantId);
        List<Permission> associatedFetchedPermissions = roleQueryPort.findPermissionsByModuleKey(moduleKey.toUpperCase());

        List<PermissionSummaryCommand> showPermissions = associatedFetchedPermissions.stream()
                .map(permission -> new PermissionSummaryCommand(
                        permission.getId(),
                        permission.getCode(),
                        permission.getDescription(),
                        permission.getAction(),
                        permission.getResource()
                )).toList();

        return new ModuleSummaryCommand(
                fetchedModule.getId(),
                fetchedModule.getName(),
                fetchedModule.getDescription(),
                fetchedModule.getModuleKey(),
                fetchedModule.getPrice(),
                fetchedModule.getCurrency(),
                fetchedModule.getIsActive(),
                showPermissions
        );
    }
}
