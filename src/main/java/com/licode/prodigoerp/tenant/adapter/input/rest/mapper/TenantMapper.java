package com.licode.prodigoerp.tenant.adapter.input.rest.mapper;

import com.licode.prodigoerp.tenant.adapter.input.rest.dto.PublicTenantEntitlementDto;
import com.licode.prodigoerp.tenant.application.port.input.command.PublicTenantEntitlementCommand;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TenantMapper {

    PublicTenantEntitlementDto toPublicTenantEntitlementDto(PublicTenantEntitlementCommand publicTenantEntitlementCommand);
}
