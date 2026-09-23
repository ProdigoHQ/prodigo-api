package com.licode.prodigoerp.module.adapter.output.persistence.subscription;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface JpaModuleSubscriptionRepository extends JpaRepository<ModuleSubscriptionJpaEntity, UUID> {

    List<ModuleSubscriptionJpaEntity> findModuleSubscriptionJpaEntityByTenantJpaEntity_Id(UUID tenantId);

    List<ModuleSubscriptionJpaEntity> findModuleSubscriptionJpaEntityByStatusAndTenantJpaEntity_Id(String status, UUID tenantJpaEntityId);
}