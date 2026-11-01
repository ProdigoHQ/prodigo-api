package com.licode.prodigoerp.auth.adapter.input.rest.mapper;

import com.licode.prodigoerp.auth.adapter.input.rest.dto.*;
import com.licode.prodigoerp.auth.application.port.input.command.*;
import com.licode.prodigoerp.auth.application.port.input.query.PermissionSummaryQuery;
import com.licode.prodigoerp.auth.application.port.input.result.RoleDetailsResult;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AuthoritiesWebMapper {

    CreatePermissionCommand toCreatePermissionCommand(CreatePermissionDto createPermissionDto);
    UpdatePermissionCommand toUpdatePermissionCommand(UpdatePermissionDto updatePermissionDto);
    SaveRoleCommand toSaveRoleCommand(SaveRoleDto saveRoleDto);

    PermissionSummaryDto toPermissionSummaryDto(PermissionSummaryQuery permissionSummaryQuery);
    List<PermissionSummaryDto> toPermissionSummaryDtos(List<PermissionSummaryQuery> permissionSummaryQueries);
    RoleSummaryDto toRoleSummaryDto(RoleDetailsResult roleDetailsResult);
    List<RoleSummaryDto> toRoleSummaryDtos(List<RoleDetailsResult> roleDetailsResults);
}
