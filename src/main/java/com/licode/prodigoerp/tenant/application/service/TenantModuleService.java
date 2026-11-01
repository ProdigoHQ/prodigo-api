package com.licode.prodigoerp.tenant.application.service;

import com.licode.prodigoerp.auth.application.helpers.AuthMappings;
import com.licode.prodigoerp.auth.application.port.output.PermissionPersistencePort;
import com.licode.prodigoerp.auth.domain.model.Permission;
import com.licode.prodigoerp.common.exception.NotFoundException;
import com.licode.prodigoerp.module.application.helpers.ModuleMappings;
import com.licode.prodigoerp.module.application.port.input.query.GetTenantModuleDetailsQuery;
import com.licode.prodigoerp.module.application.port.input.query.ListAvailableTenantModulesQuery;
import com.licode.prodigoerp.module.application.port.input.query.ListTenantActiveModulesQuery;
import com.licode.prodigoerp.module.application.port.input.result.ModuleDetailsResult;
import com.licode.prodigoerp.module.application.port.input.result.PublicModuleResult;
import com.licode.prodigoerp.module.domain.model.Module;
import com.licode.prodigoerp.tenant.application.port.input.TenantModuleUseCase;
import com.licode.prodigoerp.tenant.application.port.output.TenantModuleQueryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class TenantModuleService implements TenantModuleUseCase {

    private final TenantModuleQueryPort tenantModuleQueryPort;
    private final PermissionPersistencePort permissionPersistencePort;

    @Override
    public List<PublicModuleResult> findAllActiveModulesByTenantId(ListTenantActiveModulesQuery query) {
        return tenantModuleQueryPort.findAllActiveModulesByTenantId(query.tenantId()).stream()
                .map(ModuleMappings::toPublicSummary)
                .toList();
    }

    @Override
    public List<PublicModuleResult> findAllAvailableModulesToPay(ListAvailableTenantModulesQuery query) {
        return tenantModuleQueryPort.findAllAvailableModulesToPay(query.tenantId()).stream()
                .map(ModuleMappings::toPublicSummary)
                .toList();
    }

    @Override
    public ModuleDetailsResult findModuleWithPermissionsByKeyAndTenantId(GetTenantModuleDetailsQuery query) {

        String moduleKey = query.moduleKey();
        Optional<Module> fetchedModule = tenantModuleQueryPort.findModuleByModuleKeyAndTenantId(moduleKey.toUpperCase(), query.tenantId());

        if(fetchedModule.isEmpty()) {
            log.error("No module found with module key {} for tenant id: {}", moduleKey, query.tenantId());
            throw new NotFoundException("No Module Found with this module key: '" + moduleKey + "' among  your subscription");
        }

        List<Permission> associatedFetchedPermissions = permissionPersistencePort.findPermissionsByModuleKey(moduleKey.toUpperCase());

        return ModuleMappings.toDetails(
                fetchedModule.get(),
                associatedFetchedPermissions.stream().map(AuthMappings::toSummary).toList()
        );
    }
}
