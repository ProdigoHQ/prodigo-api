package com.licode.prodigoerp.auth.adapter.input.rest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreatePermissionDto(
        @NotNull @Size(max = 500) String description,
        @Size(max = 25) String moduleKey,
        @NotBlank @Size(max = 50) String action,
        @NotBlank @Size(max = 100) String resource
        ) {
}
