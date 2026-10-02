package com.licode.prodigoerp.tenant.adapter.output.persistence.Tenant;


import com.licode.prodigoerp.tenant.application.port.output.SaveTenantPort;
import com.licode.prodigoerp.tenant.domain.model.Tenant;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
public class TenantPersistenceAdapter implements SaveTenantPort {

    private final JpaTenantRepository jpaTenantRepository;

    @Override
    @Transactional
    public Tenant createTenant(Tenant tenant) {
        TenantJpaEntity createdTenantJpa =  jpaTenantRepository.save(TenantJpaMapper.toJpaEntity(tenant));

        return TenantJpaMapper.toDomainModel(createdTenantJpa);
    }
}
