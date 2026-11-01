package com.licode.prodigoerp.auth.adapter.input.rest.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SaveRoleRequest(
        @NotBlank @Size(max = 100) String name,
        @Size(max = 500) String description
) {
}
