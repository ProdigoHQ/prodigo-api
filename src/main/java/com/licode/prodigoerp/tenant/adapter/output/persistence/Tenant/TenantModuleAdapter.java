package com.licode.prodigoerp.tenant.adapter.output.persistence.Tenant;

import com.licode.prodigoerp.module.adapter.output.persistence.module.JpaModuleRepository;
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
    private final JpaModuleRepository jpaModuleRepository;

    @Override
    public List<Module> findAllActiveModulesByTenantId(UUID tenantId) {
        List<ModuleJpaEntity> allActiveModules = jpaTenantRepository.findModuleByTenantIdWhereStatusIsActive(tenantId);

        return allActiveModules.stream().map(ModuleJpaMapper::toDomainModel).toList();
    }

    @Override
    public List<Module> findAllAvailableModulesToPay(UUID tenantId) {
       List<ModuleJpaEntity> allAvailableModules = jpaTenantRepository.findModuleWhereTenantIdNotEquals(tenantId);

       return allAvailableModules.stream().map(ModuleJpaMapper::toDomainModel).toList();
    }

    @Override
    public Module findModuleByModuleKeyAndTenantId(String moduleKey, UUID tenantId) {
        return ModuleJpaMapper.toDomainModel(jpaModuleRepository.findModuleByModuleKeyAndTenant_Id(moduleKey, tenantId));
    }
}
