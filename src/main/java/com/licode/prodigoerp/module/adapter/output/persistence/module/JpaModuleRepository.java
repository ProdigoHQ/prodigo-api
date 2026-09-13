package com.licode.prodigoerp.module.adapter.output.persistence.module;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface JpaModuleRepository extends JpaRepository<ModuleJpaEntity, UUID> {
    Optional<ModuleJpaEntity> findModuleByModuleKey(String moduleKey);
    Optional<ModuleJpaEntity> findModuleById(UUID moduleId);

    @Query("SELECT m FROM ModuleJpaEntity m WHERE m.isActive = true")
    List<ModuleJpaEntity> findAllActiveModuleJpaEntities();

    /*
    * SELECT m.* FROM modules m
    INNER JOIN module_subscriptions ms
    ON ms.module_id = m.id
    WHERE  ms.tenant_id = '52b08839-14bb-473a-9553-2e160baa0b01'
    AND ms.status = 'ACTIVE'
    AND m.module_key = 'STOCK'
    * */
    @Query("select m from ModuleSubscriptionJpaEntity ms INNER join ModuleJpaEntity m ON ms.moduleJpaEntity.id = m.id where ms.tenantJpaEntity.id = :tenantId and ms.status = 'ACTIVE' AND m.moduleKey = :moduleKey")
    Optional<ModuleJpaEntity> findModuleByModuleKeyAndTenant_Id(String moduleKey, UUID tenantId);

}
