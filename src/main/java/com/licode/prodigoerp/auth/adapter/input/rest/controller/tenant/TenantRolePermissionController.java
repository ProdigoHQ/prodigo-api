package com.licode.prodigoerp.auth.adapter.input.rest.controller.tenant;

import com.licode.prodigoerp.auth.adapter.input.rest.dto.response.PermissionSummaryResponse;
import com.licode.prodigoerp.auth.adapter.input.rest.mapper.AuthoritiesRestMapper;
import com.licode.prodigoerp.auth.application.port.input.TenantRolePermissionUseCase;
import com.licode.prodigoerp.auth.application.port.input.query.ListRolePermissionsQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/{version}/roles/{rolesId}/permissions")
@PreAuthorize("hasAuthority('PERM_TENANT.ROLE.READ_ASSIGNMENTS')")
@RequiredArgsConstructor
public class TenantRolePermissionController {

    private final TenantRolePermissionUseCase useCase;
    private final AuthoritiesRestMapper mapper;

    @GetMapping(version = "1.0")
    public ResponseEntity<List<PermissionSummaryResponse>> list(@PathVariable UUID roleId) {

        return ResponseEntity.ok().body(
                mapper.toPermissionSummaryResponses(useCase.list(new ListRolePermissionsQuery(roleId))));
    }

    @PreAuthorize("hasAnyAuthority('PERM_TENANT.ROLE.ASSIGN', 'PERM_TENANT.ROLE.REVOKE')")
    @PostMapping(value = "/{permissionId}", version = "1.0")
    public ResponseEntity<Void> assign(@PathVariable UUID roleId, @PathVariable UUID permissionId) {
        useCase.assign(roleId, permissionId);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PreAuthorize("hasAnyAuthority('PERM_TENANT.ROLE.ASSIGN', 'PERM_TENANT.ROLE.REVOKE')")
    @DeleteMapping(value = "/{permissionId}", version = "1.0")
    public ResponseEntity<Void> remove(@PathVariable UUID roleId, @PathVariable UUID permissionId) {
        useCase.remove(roleId, permissionId);
        return ResponseEntity.noContent().build();
    }
}
