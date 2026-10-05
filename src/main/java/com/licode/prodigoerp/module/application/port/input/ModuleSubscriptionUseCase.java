package com.licode.prodigoerp.module.application.port.input;

import com.licode.prodigoerp.module.application.port.input.command.CreateModuleSubCommand;

public interface ModuleSubscriptionUseCase {

    void createModuleSubscription(CreateModuleSubCommand createModuleSubCommand);
}
