package com.licode.prodigoerp.auth.adapter.input.rest.controller.tenant;

import com.licode.prodigoerp.auth.adapter.input.rest.dto.PermissionSummaryDto;
import com.licode.prodigoerp.auth.adapter.input.rest.mapper.AuthoritiesWebMapper;
import com.licode.prodigoerp.auth.application.port.input.TenantRolePermissionUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/{version}/roles/{rolesId}/permissions")
@RequiredArgsConstructor
public class TenantRolePermissionController {

    private final TenantRolePermissionUseCase useCase;
    private final AuthoritiesWebMapper mapper;

//    @PreAuthorize("hasAuthority('AUTH.ROLE.READ')")
    @GetMapping(version = "1.0")
    public ResponseEntity<List<PermissionSummaryDto>> list(@PathVariable UUID roleId) {

        return ResponseEntity.ok().body(mapper.toPermissionSummaryDtos(useCase.list(roleId)));
    }

//    @PreAuthorize("hasAuthority('AUTH.ROLE.ASSIGN_PERMISSION')")
    @PostMapping(value = "/{permissionId}", version = "1.0")
    public ResponseEntity<Void> assign(@PathVariable UUID roleId, @PathVariable UUID permissionId) {
        useCase.assign(roleId, permissionId);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

//    @PreAuthorize("hasAuthority('AUTH.ROLE.ASSIGN_PERMISSION')")
    @DeleteMapping(value = "/{permissionId}", version = "1.0")
    public ResponseEntity<Void> remove(@PathVariable UUID roleId, @PathVariable UUID permissionId) {
        useCase.remove(roleId, permissionId);
        return ResponseEntity.noContent().build();
    }
}
