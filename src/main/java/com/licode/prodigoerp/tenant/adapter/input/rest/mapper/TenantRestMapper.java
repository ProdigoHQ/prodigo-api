package com.licode.prodigoerp.tenant.adapter.input.rest.mapper;

import com.licode.prodigoerp.tenant.adapter.input.rest.dto.response.TenantEntitlementResponse;
import com.licode.prodigoerp.tenant.application.port.input.result.TenantEntitlementResult;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TenantRestMapper {

    TenantEntitlementResponse toTenantEntitlementResponse(TenantEntitlementResult result);
}
