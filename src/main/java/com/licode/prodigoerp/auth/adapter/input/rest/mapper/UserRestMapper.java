package com.licode.prodigoerp.auth.adapter.input.rest.mapper;

import com.licode.prodigoerp.auth.adapter.input.rest.dto.request.UpdateUserRequest;
import com.licode.prodigoerp.auth.adapter.input.rest.dto.request.UpdateUserStatusRequest;
import com.licode.prodigoerp.auth.adapter.input.rest.dto.response.PermissionSummaryResponse;
import com.licode.prodigoerp.auth.adapter.input.rest.dto.response.RoleSummaryResponse;
import com.licode.prodigoerp.auth.adapter.input.rest.dto.response.UserDetailsResponse;
import com.licode.prodigoerp.auth.adapter.input.rest.dto.response.UserSummaryResponse;
import com.licode.prodigoerp.auth.application.port.input.command.UpdateUserStatusCommand;
import com.licode.prodigoerp.auth.application.port.input.command.UpdatedUserCommand;
import com.licode.prodigoerp.auth.application.port.input.result.PermissionSummaryResult;
import com.licode.prodigoerp.auth.application.port.input.result.RoleSummaryResult;
import com.licode.prodigoerp.auth.application.port.input.result.UserDetailsResult;
import com.licode.prodigoerp.auth.application.port.input.result.UserSummaryResult;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserRestMapper {

    UserSummaryResponse toUserSummaryResponse(UserSummaryResult result);
    RoleSummaryResponse toRoleSummaryResponse(RoleSummaryResult result);
    PermissionSummaryResponse toPermissionSummaryResponse(PermissionSummaryResult result);
    UserDetailsResponse toUserDetailsResponse(UserDetailsResult result);
    UpdatedUserCommand toUpdatedUserCommand(UpdateUserRequest updateUserRequest);
    UpdateUserStatusCommand toUpdateUserStatusCommand(UpdateUserStatusRequest updateUserStatusRequest);

}
