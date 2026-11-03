package com.licode.prodigoerp.tenant.adapter.output.persistence.Tenant;

import com.licode.prodigoerp.tenant.application.port.output.TenantQueryPort;
import com.licode.prodigoerp.tenant.domain.model.Tenant;
import com.licode.prodigoerp.tenant.domain.model.TenantStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class TenantQueryAdapter implements TenantQueryPort {

    private final JpaTenantRepository jpaTenantRepository;

    @Override
    public Optional<Tenant> findTenantById(UUID id) {
        Optional<TenantJpaEntity> tenantJpaEntity = jpaTenantRepository.findById(id);

       return tenantJpaEntity.map(TenantJpaMapper::toDomainModel);
    }

    @Override
    public Optional<Tenant> findTenantBySlug(String slug) {
        Optional<TenantJpaEntity> tenantJpaEntity = jpaTenantRepository.findBySlug(slug);

        return tenantJpaEntity.map(TenantJpaMapper::toDomainModel);
    }

    @Override
    public Boolean existsBySlug(String slug) {
        return jpaTenantRepository.existsBySlug(slug);
    }

    @Override
    public Boolean existsByIdAndActive(UUID id) {
        return jpaTenantRepository.existsTenantJpaEntityByIdAndStatus(id, TenantStatus.ACTIVE);
    }
}
