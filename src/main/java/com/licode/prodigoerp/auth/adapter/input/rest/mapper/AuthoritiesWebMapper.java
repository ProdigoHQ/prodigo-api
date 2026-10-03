package com.licode.prodigoerp.auth.adapter.input.rest.mapper;

import com.licode.prodigoerp.auth.adapter.input.rest.dto.CreatePermissionDto;
import com.licode.prodigoerp.auth.adapter.input.rest.dto.PermissionSummaryDto;
import com.licode.prodigoerp.auth.application.port.input.command.CreatePermissionCommand;
import com.licode.prodigoerp.auth.application.port.input.command.PermissionSummaryCommand;
import com.licode.prodigoerp.auth.domain.model.Permission;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AuthoritiesWebMapper {

    PermissionSummaryDto toPermissionSummaryDto(PermissionSummaryCommand permissionSummaryCommand);
    PermissionSummaryDto toPermissionSummaryDto(Permission permission);
    CreatePermissionCommand toCreatePermissionCommand(CreatePermissionDto createPermissionDto);
}
