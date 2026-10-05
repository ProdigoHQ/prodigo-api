package com.licode.prodigoerp.module.application.service;

import com.licode.prodigoerp.common.shared.application.output.CurrentUserPort;
import com.licode.prodigoerp.module.application.port.input.ModuleSubscriptionUseCase;
import com.licode.prodigoerp.module.application.port.output.ModuleSubscriptionCreatePort;
import com.licode.prodigoerp.module.application.port.input.command.CreateModuleSubCommand;
import com.licode.prodigoerp.module.domain.model.ModuleSubscription;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class ModuleSubCommandService implements ModuleSubscriptionUseCase {

    private final ModuleSubscriptionCreatePort moduleSubscriptionCreatePort;
    private final CurrentUserPort currentUser;

    @Override
    public void createModuleSubscription(CreateModuleSubCommand createModuleSubCommand) {

        String actor = currentUser.usernameOrSystem();

        Instant now = Instant.now();

        ModuleSubscription moduleSubscription = new ModuleSubscription();
        moduleSubscription.setId(null);
        moduleSubscription.setModule(createModuleSubCommand.module());
        moduleSubscription.setTenant(createModuleSubCommand.tenant());
        moduleSubscription.setStatus("ACTIVE");
        moduleSubscription.setIsFree(createModuleSubCommand.isFree());

        // TODO: Currency and price need to depend on the Country of the user
        moduleSubscription.setPrice(createModuleSubCommand.price());
        moduleSubscription.setCurrency(createModuleSubCommand.currency());

        moduleSubscription.setActivatedAt(now);
        moduleSubscription.setExpiresAt(createModuleSubCommand.isFree()
                ? null
                : now.plusSeconds(2592000));

        moduleSubscription.setCreatedAt(now);
        moduleSubscription.setUpdatedAt(now);
        moduleSubscription.setCreatedBy(actor);
        moduleSubscription.setUpdatedBy(actor);

        moduleSubscriptionCreatePort.create(moduleSubscription);
    }
}
