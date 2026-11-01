package com.licode.prodigoerp.auth.adapter.input.rest.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

public record RegisterUserRequest(
        @NotBlank String companyName,
        @NotBlank String companySlug,
        @NotBlank String country,
        @NotBlank String username,
        @NotBlank @Email String email,
        @NotBlank String password,
        @NotBlank String firstName,
        @NotBlank String lastName,
        List<SelectedModuleRequest> selectedModules
) {
}
