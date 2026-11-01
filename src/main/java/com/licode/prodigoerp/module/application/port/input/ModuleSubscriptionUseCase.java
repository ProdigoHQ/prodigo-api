package com.licode.prodigoerp.module.application.port.input;

import com.licode.prodigoerp.module.application.port.input.command.CreateModuleSubscriptionCommand;

public interface ModuleSubscriptionUseCase {

    void createModuleSubscription(CreateModuleSubscriptionCommand createModuleSubscriptionCommand);
}
