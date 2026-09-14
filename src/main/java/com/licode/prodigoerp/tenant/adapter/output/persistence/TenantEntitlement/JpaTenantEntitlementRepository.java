package com.licode.prodigoerp.tenant.adapter.output.persistence.TenantEntitlement;

import com.licode.prodigoerp.tenant.domain.model.TenantEntitlement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface JpaTenantEntitlementRepository extends JpaRepository<TenantEntitlementJpaEntity, UUID> {

    Optional<TenantEntitlementJpaEntity> findTenantEntitlementJpaEntitiesByTenantJpaEntity_Id(UUID tenantJpaEntityId);
}
