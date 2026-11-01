package com.licode.prodigoerp.module.adapter.input.rest.dto.response;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public record PublicModuleResponse(
        @NotNull UUID id,
        @NotBlank String name,
        @NotBlank String description,
        @NotBlank String moduleKey,
        @NotBlank BigDecimal price,
        @NotBlank String currency
) {
}
