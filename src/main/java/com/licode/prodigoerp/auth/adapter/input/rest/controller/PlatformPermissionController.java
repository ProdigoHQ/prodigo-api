package com.licode.prodigoerp.auth.adapter.input.rest.controller;

import com.licode.prodigoerp.auth.adapter.input.rest.dto.CreatePermissionDto;
import com.licode.prodigoerp.auth.adapter.input.rest.dto.PermissionSummaryDto;
import com.licode.prodigoerp.auth.adapter.input.rest.mapper.AuthoritiesWebMapper;
import com.licode.prodigoerp.auth.application.port.input.AuthoritiesUseCase;
import com.licode.prodigoerp.auth.application.port.input.command.PermissionSummaryCommand;
import com.licode.prodigoerp.auth.domain.model.Permission;
import com.licode.prodigoerp.common.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/{version}/s/admin/permissions")
@RequiredArgsConstructor
public class PlatformPermissionController {

    private final AuthoritiesWebMapper authoritiesWebMapper;
    private final AuthoritiesUseCase authoritiesUseCase;


    @GetMapping(value = "/{permissionId}", version = "1.0")
    public ResponseEntity<PermissionSummaryDto> getPermissions(@PathVariable UUID permissionId) {

        PermissionSummaryCommand permissionDetails = authoritiesUseCase.fetchPermissionSummary(permissionId);

        return ResponseEntity.ok(authoritiesWebMapper.toPermissionSummaryDto(permissionDetails));
    }

    @PostMapping(value = "/", version = "1.0")
    public ResponseEntity<PermissionSummaryDto> createPermission(@RequestBody CreatePermissionDto createPermissionDto) {

        String currentUserLogin = SecurityUtils.getCurrentUser().username();

        Permission createdPermission = authoritiesUseCase.savePermission(
                authoritiesWebMapper.toCreatePermissionCommand(createPermissionDto),
                currentUserLogin);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(authoritiesWebMapper.toPermissionSummaryDto(createdPermission));
    }

    @DeleteMapping(value = "/{permissionId}", version = "1.0")
    public ResponseEntity<Void> deletePermission(@PathVariable UUID permissionId) {
        // TODO: two paths to verify : soft delete and also check for system permissions that should never be deleted

        authoritiesUseCase.deletePermission(permissionId);

        return ResponseEntity.ok().build();
    }
}
