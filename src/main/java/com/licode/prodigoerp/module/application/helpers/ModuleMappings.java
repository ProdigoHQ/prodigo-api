package com.licode.prodigoerp.module.application.helpers;

import com.licode.prodigoerp.auth.application.port.input.result.PermissionSummaryResult;
import com.licode.prodigoerp.module.application.port.input.result.ModuleDetailsResult;
import com.licode.prodigoerp.module.application.port.input.result.PublicModuleResult;
import com.licode.prodigoerp.module.domain.model.Module;

import java.util.List;

public class ModuleMappings {
    private ModuleMappings() {}

    public static PublicModuleResult toPublicSummary(Module module) {
        return new PublicModuleResult(
                module.getId(),
                module.getName(),
                module.getDescription(),
                module.getModuleKey(),
                module.getPrice(),
                module.getCurrency()
        );
    }

    public static ModuleDetailsResult toDetails(Module module, List<PermissionSummaryResult> permissions) {
        return new ModuleDetailsResult(
                module.getId(),
                module.getName(),
                module.getDescription(),
                module.getModuleKey(),
                module.getPrice(),
                module.getCurrency(),
                module.getIsActive(),
                permissions
        );
    }
}
