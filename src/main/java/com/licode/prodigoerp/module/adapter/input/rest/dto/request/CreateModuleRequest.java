package com.licode.prodigoerp.module.adapter.input.rest.dto.request;

import com.licode.prodigoerp.auth.adapter.input.rest.dto.request.CreatePermissionRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.List;

public record CreateModuleRequest(
        @NotBlank @Size(max = 150) String name,
        @NotBlank @Size(max = 255) String description,
        @NotBlank @Size(max = 20) String moduleKey,
        @NotNull BigDecimal price,
        @NotNull @Valid List<CreatePermissionRequest> createPermissions
) {
}
