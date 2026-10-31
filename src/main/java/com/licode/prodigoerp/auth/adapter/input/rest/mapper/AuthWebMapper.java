package com.licode.prodigoerp.auth.adapter.input.rest.mapper;

import com.licode.prodigoerp.auth.adapter.input.rest.dto.*;
import com.licode.prodigoerp.auth.application.port.input.command.*;
import com.licode.prodigoerp.module.application.port.input.command.SelectedModuleCommand;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AuthWebMapper {


    RegisterUserCommand toRegisterUserCommand(RegisterRequestDto registerRequestDto);
    AuthResponseDto toAuthResponseDto(AuthResponseCommand authResponseCommand);
    RefreshResponseDto toRefreshResponseDto(RefreshResponseCommand refreshResponseCommand);
    SelectedModuleCommand toSelectedModuleCommand(RegisterSelectedModuleDto registerSelectedModuleDto);
    RegisterSelectedModuleDto toRegisterSelectedModuleDto(SelectedModuleCommand selectedModuleCommand);
    RegisterPlatformAdminCommand toRegisterSuperAdminCommand(CreatePlatformAdminDto createSuperAdminDto);
}
