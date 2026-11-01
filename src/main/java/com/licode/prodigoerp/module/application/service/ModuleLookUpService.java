package com.licode.prodigoerp.module.application.service;

import com.licode.prodigoerp.module.application.helpers.ModuleMappings;
import com.licode.prodigoerp.module.application.port.input.AllPublicModuleLookUp;
import com.licode.prodigoerp.module.application.port.input.ModuleLookUpUseCase;
import com.licode.prodigoerp.module.application.port.input.result.PublicModuleResult;
import com.licode.prodigoerp.module.application.port.output.ModuleQueryPort;
import com.licode.prodigoerp.module.domain.model.Module;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ModuleLookUpService implements ModuleLookUpUseCase, AllPublicModuleLookUp {

    private final ModuleQueryPort moduleQueryPort;

    @Override
    public Optional<Module> findModuleByModuleKey(String moduleKey) {
        return moduleQueryPort.findModuleByModuleKey(moduleKey);
    }


    @Override
    public List<PublicModuleResult> findAllPublicModules() {

        return moduleQueryPort.findAllActivePublicModules().stream()
                .map(ModuleMappings::toPublicSummary)
                .toList();
    }
}
