package com.licode.prodigoerp.auth.adapter.input.rest.mapper;

import com.licode.prodigoerp.auth.adapter.input.rest.dto.request.CreateAdminRequest;
import com.licode.prodigoerp.auth.adapter.input.rest.dto.request.RegisterUserRequest;
import com.licode.prodigoerp.auth.adapter.input.rest.dto.request.SelectedModuleRequest;
import com.licode.prodigoerp.auth.adapter.input.rest.dto.response.AuthResponse;
import com.licode.prodigoerp.auth.adapter.input.rest.dto.response.RefreshResponse;
import com.licode.prodigoerp.auth.application.port.input.command.CreateAdminCommand;
import com.licode.prodigoerp.auth.application.port.input.command.RegisterUserCommand;
import com.licode.prodigoerp.auth.application.port.input.result.AuthResult;
import com.licode.prodigoerp.auth.application.port.input.result.RefreshTokenResult;
import com.licode.prodigoerp.module.application.port.input.command.SelectedModuleCommand;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AuthRestMapper {

    RegisterUserCommand toRegisterUserCommand(RegisterUserRequest request);

    AuthResponse toAuthResponse(AuthResult result);

    RefreshResponse toRefreshResponse(RefreshTokenResult result);

    SelectedModuleCommand toSelectedModuleCommand(SelectedModuleRequest request);

    CreateAdminCommand toCreateAdminCommand(CreateAdminRequest request);
}
