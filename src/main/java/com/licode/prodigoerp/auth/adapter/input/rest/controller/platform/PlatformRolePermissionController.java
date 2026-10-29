package com.licode.prodigoerp.auth.adapter.input.rest.controller.platform;

import com.licode.prodigoerp.auth.adapter.input.rest.dto.PermissionSummaryDto;
import com.licode.prodigoerp.auth.adapter.input.rest.mapper.AuthoritiesWebMapper;
import com.licode.prodigoerp.auth.application.port.input.SystemRolePermissionUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/{version}/s/admin/roles/{roleId}/permissions")
@RequiredArgsConstructor
public class PlatformRolePermissionController {

    private final SystemRolePermissionUseCase useCase;
    private final AuthoritiesWebMapper mapper;

    @PreAuthorize("hasAuthority('ERP.ADMIN.READ')")
    @GetMapping(version = "1.0")
    public ResponseEntity<List<PermissionSummaryDto>> list(@PathVariable UUID roleId) {
        return ResponseEntity.ok().body(mapper.toPermissionSummaryDtos(useCase.list(roleId)));
    }

    @PreAuthorize("hasAnyAuthority('ERP.ADMIN.CREATE', 'ERP.ADMIN.UPDATE', 'ERP.ADMIN.DELETE')")
    @PostMapping(value = "/{permissionId}", version = "1.0")
    public ResponseEntity<Void> assign(@PathVariable UUID roleId, @PathVariable UUID permissionId) {

        useCase.assign(roleId, permissionId);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PreAuthorize("hasAnyAuthority('ERP.ADMIN.CREATE', 'ERP.ADMIN.UPDATE', 'ERP.ADMIN.DELETE')")
    @DeleteMapping(value = "/{permissionId}", version = "1.0")
    public ResponseEntity<Void> remove(@PathVariable UUID roleId, @PathVariable UUID permissionId) {
        useCase.remove(roleId, permissionId);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
