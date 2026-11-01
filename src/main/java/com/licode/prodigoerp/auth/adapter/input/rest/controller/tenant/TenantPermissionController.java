package com.licode.prodigoerp.auth.adapter.input.rest.controller.tenant;


import com.licode.prodigoerp.auth.adapter.input.rest.dto.response.PermissionSummaryResponse;
import com.licode.prodigoerp.auth.adapter.input.rest.mapper.AuthoritiesRestMapper;
import com.licode.prodigoerp.auth.application.port.input.PermissionQueryUseCase;
import com.licode.prodigoerp.auth.application.port.input.query.GetPermissionSummaryQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/{version}/permissions")
@PreAuthorize("hasAuthority('PERM_TENANT.PERMISSION.READ')")
@RequiredArgsConstructor
public class TenantPermissionController {

    private final PermissionQueryUseCase useCase;
    private final AuthoritiesRestMapper mapper;

    @GetMapping(version = "1.0")
    public ResponseEntity<List<PermissionSummaryResponse>> list(){
        return ResponseEntity.ok().body(mapper.toPermissionSummaryResponses(useCase.list()));
    }

    @GetMapping(value = "/{permissionId}", version = "1.0")
    public ResponseEntity<PermissionSummaryResponse> get(@PathVariable UUID permissionId){
        return ResponseEntity.ok().body(mapper.toPermissionSummaryResponse(
                useCase.get(new GetPermissionSummaryQuery(permissionId))));
    }
}
