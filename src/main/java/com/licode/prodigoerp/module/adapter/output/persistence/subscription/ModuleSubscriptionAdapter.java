package com.licode.prodigoerp.module.adapter.output.persistence.subscription;

import com.licode.prodigoerp.module.application.port.output.ModuleSubscriptionCreatePort;
import com.licode.prodigoerp.module.domain.model.ModuleSubscription;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
public class ModuleSubscriptionAdapter implements ModuleSubscriptionCreatePort {

    private final JpaModuleSubscriptionRepository jpaModuleSubscriptionRepository;

    @Override
    @Transactional
    public ModuleSubscription create(ModuleSubscription moduleSubscription) {

        ModuleSubscriptionJpaEntity moduleSubscriptionJpaEntity = jpaModuleSubscriptionRepository
                .save(ModuleSubscriptionJpaMapper.toJpaEntity(moduleSubscription));

        return ModuleSubscriptionJpaMapper.toDomainModel(moduleSubscriptionJpaEntity);
    }
}
