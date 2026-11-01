package com.licode.prodigoerp.module.application.port.input.command;

import com.licode.prodigoerp.module.domain.model.Module;
import com.licode.prodigoerp.tenant.domain.model.Tenant;

import java.math.BigDecimal;

public record CreateModuleSubscriptionCommand(
        Tenant tenant,
        Module module,
        Boolean isFree,
        BigDecimal price,
        String currency

) {
}
