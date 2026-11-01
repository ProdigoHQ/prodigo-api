package com.licode.prodigoerp.tenant.application.port.input;

import com.licode.prodigoerp.module.application.port.input.query.GetTenantModuleDetailsQuery;
import com.licode.prodigoerp.module.application.port.input.query.ListAvailableTenantModulesQuery;
import com.licode.prodigoerp.module.application.port.input.query.ListTenantActiveModulesQuery;
import com.licode.prodigoerp.module.application.port.input.result.ModuleDetailsResult;
import com.licode.prodigoerp.module.application.port.input.result.PublicModuleResult;

import java.util.List;

public interface TenantModuleUseCase {
    List<PublicModuleResult> findAllActiveModulesByTenantId(ListTenantActiveModulesQuery query);
    List<PublicModuleResult> findAllAvailableModulesToPay(ListAvailableTenantModulesQuery query);
    ModuleDetailsResult findModuleWithPermissionsByKeyAndTenantId(GetTenantModuleDetailsQuery query);
}
