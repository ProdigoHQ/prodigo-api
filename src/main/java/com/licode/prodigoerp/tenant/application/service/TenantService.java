package com.licode.prodigoerp.tenant.application.service;

import com.licode.prodigoerp.tenant.application.port.input.CreateTenantUseCase;
import com.licode.prodigoerp.tenant.application.port.output.SaveTenantPort;
import com.licode.prodigoerp.tenant.application.port.input.command.CreateTenantCommand;
import com.licode.prodigoerp.tenant.domain.model.Tenant;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;

@RequiredArgsConstructor
public class TenantService implements CreateTenantUseCase {
    private final SaveTenantPort saveTenantPort;

    @Override
    public Tenant create(CreateTenantCommand command) {

        String actor = "PRODIGO_EPR_API"; // TODO
        Instant now = Instant.now();

        Tenant tenant = new Tenant();
        tenant.setId(null);
        tenant.setName(command.companyName());
        tenant.setSlug(command.companySlug());
        tenant.setCountry(command.country());
        tenant.setStatus("ACTIVE");

        tenant.setCreatedAt(now);
        tenant.setUpdatedAt(now);
        tenant.setCreatedBy(actor);
        tenant.setUpdatedBy(actor);

        return saveTenantPort.createTenant(tenant);
    }
}
