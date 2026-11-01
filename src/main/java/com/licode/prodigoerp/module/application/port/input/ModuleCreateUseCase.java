package com.licode.prodigoerp.module.application.port.input;

import com.licode.prodigoerp.module.application.port.input.command.CreateModuleCommand;
import com.licode.prodigoerp.module.application.port.input.result.ModuleDetailsResult;

public interface ModuleCreateUseCase {

    ModuleDetailsResult createModule(CreateModuleCommand command);
}
