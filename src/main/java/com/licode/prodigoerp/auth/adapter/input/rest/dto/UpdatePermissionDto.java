package com.licode.prodigoerp.auth.adapter.input.rest.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UpdatePermissionDto(
        @NotNull @Size(max = 500) String description
) {
}
