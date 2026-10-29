package com.licode.prodigoerp.auth.adapter.input.rest.controller.tenant;


import com.licode.prodigoerp.auth.adapter.input.rest.dto.PermissionSummaryDto;
import com.licode.prodigoerp.auth.adapter.input.rest.mapper.AuthoritiesWebMapper;
import com.licode.prodigoerp.auth.application.port.input.PermissionQueryUseCase;
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
@PreAuthorize("hasAuthority('TENANT.PERMISSION.READ')")
@RequiredArgsConstructor
public class TenantPermissionController {

    private final PermissionQueryUseCase useCase;
    private final AuthoritiesWebMapper mapper;

    @GetMapping(version = "1.0")
    public ResponseEntity<List<PermissionSummaryDto>> list(){
        return ResponseEntity.ok().body(mapper.toPermissionSummaryDtos(useCase.list()));
    }

    @GetMapping(value = "/{permissionId}", version = "1.0")
    public ResponseEntity<PermissionSummaryDto> get(@PathVariable UUID permissionId){
        return ResponseEntity.ok().body(mapper.toPermissionSummaryDto(useCase.get(permissionId)));
    }
}
