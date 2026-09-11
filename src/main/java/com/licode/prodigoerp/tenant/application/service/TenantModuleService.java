package com.licode.prodigoerp.tenant.application.service;

import com.licode.prodigoerp.tenant.application.port.input.TenantModuleUseCase;
import com.licode.prodigoerp.module.application.port.input.command.ShowPublicModuleCommand;
import com.licode.prodigoerp.tenant.application.port.output.TenantModuleQueryPort;
import com.licode.prodigoerp.module.domain.model.Module;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TenantModuleService implements TenantModuleUseCase {

    private final TenantModuleQueryPort tenantModuleQueryPort;

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
}
