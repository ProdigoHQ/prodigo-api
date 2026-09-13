package com.licode.prodigoerp.tenant.application.port.output;

import com.licode.prodigoerp.module.domain.model.Module;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TenantModuleQueryPort {
    List<Module> findAllActiveModulesByTenantId(UUID tenantId);
    List<Module> findAllAvailableModulesToPay(UUID tenantId);
    Optional<Module> findModuleByModuleKeyAndTenantId(String moduleKey, UUID tenantId);
}
