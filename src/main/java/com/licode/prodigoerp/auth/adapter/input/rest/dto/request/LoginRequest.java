package com.licode.prodigoerp.auth.adapter.input.rest.dto.request;

import jakarta.validation.constraints.NotBlank;


public record LoginRequest(
        @NotBlank String username,
        @NotBlank String password
) {
}
