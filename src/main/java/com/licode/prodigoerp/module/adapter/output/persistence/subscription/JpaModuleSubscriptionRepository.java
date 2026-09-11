package com.licode.prodigoerp.module.adapter.output.persistence.subscription;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface JpaModuleSubscriptionRepository extends JpaRepository<ModuleSubscriptionJpaEntity, UUID> {
}