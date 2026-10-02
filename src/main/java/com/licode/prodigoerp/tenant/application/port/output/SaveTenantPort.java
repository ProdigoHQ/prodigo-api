package com.licode.prodigoerp.tenant.application.port.output;


import com.licode.prodigoerp.tenant.domain.model.Tenant;

public interface SaveTenantPort {


      Tenant createTenant(Tenant tenant);
}
