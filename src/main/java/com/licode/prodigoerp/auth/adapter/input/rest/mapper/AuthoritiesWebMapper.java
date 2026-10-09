package com.licode.prodigoerp.auth.adapter.input.rest.mapper;

import com.licode.prodigoerp.auth.adapter.input.rest.dto.*;
import com.licode.prodigoerp.auth.application.port.input.command.*;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AuthoritiesWebMapper {

    CreatePermissionCommand toCreatePermissionCommand(CreatePermissionDto createPermissionDto);
    UpdatePermissionCommand toUpdatePermissionCommand(UpdatePermissionDto updatePermissionDto);
    SaveRoleCommand toSaveRoleCommand(SaveRoleDto saveRoleDto);

    PermissionSummaryDto toPermissionSummaryDto(PermissionSummaryCommand permissionSummaryCommand);
    List<PermissionSummaryDto> toPermissionSummaryDtos(List<PermissionSummaryCommand> permissionSummaryCommands);
    RoleSummaryDto toRoleSummaryDto(RoleSummaryCommand roleSummaryCommand);
    List<RoleSummaryDto> toRoleSummaryDtos(List<RoleSummaryCommand> roleSummaryCommands);
}
