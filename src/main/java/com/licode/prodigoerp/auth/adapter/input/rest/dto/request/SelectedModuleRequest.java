package com.licode.prodigoerp.auth.adapter.input.rest.dto.request;


import java.util.UUID;

public record SelectedModuleRequest(
        UUID moduleId,
        String moduleName,
        String moduleKey
) {

}
