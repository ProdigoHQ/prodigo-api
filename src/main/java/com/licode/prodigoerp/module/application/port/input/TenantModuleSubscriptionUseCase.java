package com.licode.prodigoerp.module.application.port.input;

import com.licode.prodigoerp.module.application.port.input.command.SelectedModuleCommand;
import com.licode.prodigoerp.module.domain.model.Module;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface TenantModuleSubscriptionUseCase {

    Map<String, Module> createTenantModuleSubscription(UUID tenantId, List<SelectedModuleCommand> selectedModuleCommands);
}
