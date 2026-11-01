package com.licode.prodigoerp.auth.adapter.input.rest.mapper;

import com.licode.prodigoerp.auth.adapter.input.rest.dto.request.CreatePermissionRequest;
import com.licode.prodigoerp.auth.adapter.input.rest.dto.request.SaveRoleRequest;
import com.licode.prodigoerp.auth.adapter.input.rest.dto.request.UpdatePermissionRequest;
import com.licode.prodigoerp.auth.adapter.input.rest.dto.response.PermissionSummaryResponse;
import com.licode.prodigoerp.auth.adapter.input.rest.dto.response.RoleDetailsResponse;
import com.licode.prodigoerp.auth.application.port.input.command.CreatePermissionCommand;
import com.licode.prodigoerp.auth.application.port.input.command.SaveRoleCommand;
import com.licode.prodigoerp.auth.application.port.input.command.UpdatePermissionCommand;
import com.licode.prodigoerp.auth.application.port.input.result.PermissionSummaryResult;
import com.licode.prodigoerp.auth.application.port.input.result.RoleDetailsResult;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AuthoritiesRestMapper {

    CreatePermissionCommand toCreatePermissionCommand(CreatePermissionRequest request);

    UpdatePermissionCommand toUpdatePermissionCommand(UpdatePermissionRequest request);

    SaveRoleCommand toSaveRoleCommand(SaveRoleRequest request);

    PermissionSummaryResponse toPermissionSummaryResponse(PermissionSummaryResult result);

    List<PermissionSummaryResponse> toPermissionSummaryResponses(List<PermissionSummaryResult> results);

    RoleDetailsResponse toRoleDetailsResponse(RoleDetailsResult result);

    List<RoleDetailsResponse> toRoleDetailsResponses(List<RoleDetailsResult> results);
}
