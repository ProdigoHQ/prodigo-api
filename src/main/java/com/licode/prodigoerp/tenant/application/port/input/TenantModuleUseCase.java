package com.licode.prodigoerp.tenant.application.port.input;

import com.licode.prodigoerp.module.application.port.input.command.ShowPublicModuleCommand;

import java.util.List;
import java.util.UUID;

public interface TenantModuleUseCase {
    List<ShowPublicModuleCommand> findAllActiveModulesByTenantId(UUID tenantId);

    List<ShowPublicModuleCommand> findAllAvailableModulesToPay(UUID tenantId);
}
