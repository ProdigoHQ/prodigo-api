package com.licode.prodigoerp.module.application.service;

import com.licode.prodigoerp.common.shared.application.output.CurrentUserPort;
import com.licode.prodigoerp.module.application.port.input.ModuleSubscriptionUseCase;
import com.licode.prodigoerp.module.application.port.output.ModuleSubscriptionCreatePort;
import com.licode.prodigoerp.module.application.port.input.command.CreateModuleSubscriptionCommand;
import com.licode.prodigoerp.module.domain.model.ModuleSubscription;
import com.licode.prodigoerp.module.domain.model.SubscriptionStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class ModuleSubscriptionService implements ModuleSubscriptionUseCase {

    private final ModuleSubscriptionCreatePort moduleSubscriptionCreatePort;
    private final CurrentUserPort currentUser;

    @Override
    public void createModuleSubscription(CreateModuleSubscriptionCommand createModuleSubscriptionCommand) {

        String actor = currentUser.usernameOrSystem();

        Instant now = Instant.now();

        ModuleSubscription moduleSubscription = new ModuleSubscription();
        moduleSubscription.setId(null);
        moduleSubscription.setModule(createModuleSubscriptionCommand.module());
        moduleSubscription.setTenant(createModuleSubscriptionCommand.tenant());
        moduleSubscription.setStatus(SubscriptionStatus.ACTIVE);
        moduleSubscription.setIsFree(createModuleSubscriptionCommand.isFree());

        // TODO: Currency and price need to depend on the Country of the user
        moduleSubscription.setPrice(createModuleSubscriptionCommand.price());
        moduleSubscription.setCurrency(createModuleSubscriptionCommand.currency());

        moduleSubscription.setActivatedAt(now);
        moduleSubscription.setExpiresAt(createModuleSubscriptionCommand.isFree()
                ? null
                : now.plusSeconds(2592000));

        moduleSubscription.setCreatedAt(now);
        moduleSubscription.setUpdatedAt(now);
        moduleSubscription.setCreatedBy(actor);
        moduleSubscription.setUpdatedBy(actor);

        moduleSubscriptionCreatePort.create(moduleSubscription);
    }
}
