package com.licode.prodigoerp.tenant.adapter.input.rest.controller;

import com.licode.prodigoerp.common.config.TenantContext;
import com.licode.prodigoerp.tenant.adapter.input.rest.dto.response.TenantEntitlementResponse;
import com.licode.prodigoerp.tenant.adapter.input.rest.mapper.TenantRestMapper;
import com.licode.prodigoerp.tenant.application.port.input.TenantEntitlementUseCase;
import com.licode.prodigoerp.tenant.application.port.input.query.GetTenantEntitlementQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/{version}/tenant")
@RequiredArgsConstructor
public class TenantController {

    private final TenantRestMapper tenantRestMapper;
    private final TenantEntitlementUseCase tenantEntitlementUseCase;

    @GetMapping(path = "/entitlements", version = "1.0")
    public ResponseEntity<TenantEntitlementResponse> getEntitlements() {

        UUID tenantId = TenantContext.getCurrentTenant();

        return ResponseEntity.ok().body(
                tenantRestMapper.toTenantEntitlementResponse(
                        tenantEntitlementUseCase.findEntitlementByUuid(new GetTenantEntitlementQuery(tenantId))
                )
        );
    }
}
