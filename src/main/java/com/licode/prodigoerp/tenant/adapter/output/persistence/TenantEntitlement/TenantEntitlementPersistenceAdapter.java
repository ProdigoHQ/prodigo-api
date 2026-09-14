package com.licode.prodigoerp.tenant.adapter.output.persistence.TenantEntitlement;

import com.licode.prodigoerp.tenant.application.port.output.TenantEntitlementPort;
import com.licode.prodigoerp.tenant.domain.model.TenantEntitlement;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;


@Repository
@RequiredArgsConstructor
public class TenantEntitlementPersistenceAdapter implements TenantEntitlementPort {

    private final JpaTenantEntitlementRepository jpaTenantEntitlementRepository;


    @Override
    @Transactional
    public TenantEntitlement createDefaultTenantEntitlement(TenantEntitlement tenantEntitlement) {
        TenantEntitlementJpaEntity tenantEntitlementJpaEntity = jpaTenantEntitlementRepository.save(TenantEntitlementJpaMapper.toDbCreateTenantEntitlement(tenantEntitlement));

        return TenantEntitlementJpaMapper.toDomainModel(tenantEntitlementJpaEntity);
    }

    @Override
    public Optional<TenantEntitlement> findEntitlementById(UUID tenantId) {
        Optional<TenantEntitlementJpaEntity>  tenantEntitlementJpaEntity = jpaTenantEntitlementRepository.findTenantEntitlementJpaEntitiesByTenantJpaEntity_Id(tenantId);

        return  tenantEntitlementJpaEntity.map(TenantEntitlementJpaMapper::toDomainModel);
    }
}
