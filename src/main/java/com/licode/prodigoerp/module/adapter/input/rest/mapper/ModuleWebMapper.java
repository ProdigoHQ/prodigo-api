package com.licode.prodigoerp.module.adapter.input.rest.mapper;

import com.licode.prodigoerp.auth.adapter.input.rest.dto.CreatePermissionDto;
import com.licode.prodigoerp.auth.adapter.input.rest.dto.PermissionSummaryDto;
import com.licode.prodigoerp.auth.application.port.input.command.CreatePermissionCommand;
import com.licode.prodigoerp.auth.application.port.input.command.PermissionSummaryCommand;
import com.licode.prodigoerp.module.adapter.input.rest.dto.ModuleResponseDto;
import com.licode.prodigoerp.module.adapter.input.rest.dto.RegisterModuleDto;
import com.licode.prodigoerp.module.adapter.input.rest.dto.ShowPublicModuleDto;
import com.licode.prodigoerp.module.application.port.input.command.ModuleSummaryCommand;
import com.licode.prodigoerp.module.application.port.input.command.RegisterModuleCommand;
import com.licode.prodigoerp.module.application.port.input.command.ShowPublicModuleCommand;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ModuleWebMapper {

    ShowPublicModuleDto toShowPublicModuleDto(ShowPublicModuleCommand showPublicModuleCommand);
    RegisterModuleCommand toRegisterModuleCommand(RegisterModuleDto registerModuleDto);
    CreatePermissionDto toCreatePermissionDto(CreatePermissionCommand createPermissionCommand);
    CreatePermissionCommand toCreatePermissionCommand(CreatePermissionDto createPermissionDto);
    ModuleResponseDto toModuleResponseDto(ModuleSummaryCommand moduleSummaryCommand);
    PermissionSummaryDto toPermissionSummaryDto(PermissionSummaryCommand permissionSummaryCommand);
}
