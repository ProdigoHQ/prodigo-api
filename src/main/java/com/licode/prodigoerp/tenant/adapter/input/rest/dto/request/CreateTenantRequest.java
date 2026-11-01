package com.licode.prodigoerp.tenant.adapter.input.rest.dto.request;

public record CreateTenantRequest(
        String companyName,
        String companySlug,
        String country
) {
}
