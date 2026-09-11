package com.licode.prodigoerp.tenant.adapter.output.persistence.Tenant;

import com.licode.prodigoerp.module.adapter.output.persistence.module.ModuleJpaEntity;
import com.licode.prodigoerp.module.adapter.output.persistence.module.ModuleJpaMapper;
import com.licode.prodigoerp.module.domain.model.Module;
import com.licode.prodigoerp.tenant.application.port.output.TenantModuleQueryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class TenantModuleAdapter implements TenantModuleQueryPort {

    private final JpaTenantRepository jpaTenantRepository;

    @Override
    public List<Module> findAllActiveModulesByTenantId(UUID tenantId) {
        List<ModuleJpaEntity> allActiveModules = jpaTenantRepository.findModuleByTenantIdWhereStatusIsActive(tenantId);

        return allActiveModules.stream().map(ModuleJpaMapper::toDomainModel).toList();
    }
}
