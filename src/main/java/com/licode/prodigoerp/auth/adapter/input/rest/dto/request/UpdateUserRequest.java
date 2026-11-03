package com.licode.prodigoerp.auth.adapter.input.rest.dto.request;

import jakarta.validation.constraints.NotBlank;

public record UpdateUserRequest(
        @NotBlank String firstName,
        @NotBlank String lastName
) {
}
