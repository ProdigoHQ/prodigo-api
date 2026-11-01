package com.licode.prodigoerp.module.adapter.input.rest.mapper;

import com.licode.prodigoerp.module.adapter.input.rest.dto.request.CreateModuleRequest;
import com.licode.prodigoerp.module.adapter.input.rest.dto.response.ModuleDetailsResponse;
import com.licode.prodigoerp.module.adapter.input.rest.dto.response.PublicModuleResponse;
import com.licode.prodigoerp.module.application.port.input.command.CreateModuleCommand;
import com.licode.prodigoerp.module.application.port.input.result.ModuleDetailsResult;
import com.licode.prodigoerp.module.application.port.input.result.PublicModuleResult;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ModuleRestMapper {

    PublicModuleResponse toPublicModuleResponse(PublicModuleResult result);

    CreateModuleCommand toCreateModuleCommand(CreateModuleRequest request);

    ModuleDetailsResponse toModuleDetailsResponse(ModuleDetailsResult result);
}
