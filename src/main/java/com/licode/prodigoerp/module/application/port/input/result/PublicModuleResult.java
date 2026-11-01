package com.licode.prodigoerp.module.application.port.input.result;

import java.math.BigDecimal;
import java.util.UUID;

public record PublicModuleResult(
        UUID id,
         String name,
         String description,
         String moduleKey,
         BigDecimal price,
         String currency
) {
}
