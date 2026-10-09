package com.licode.prodigoerp.auth.adapter.input.rest.controller.platform;

import com.licode.prodigoerp.auth.adapter.input.rest.dto.CreatePermissionDto;
import com.licode.prodigoerp.auth.adapter.input.rest.dto.PermissionSummaryDto;
import com.licode.prodigoerp.auth.adapter.input.rest.dto.UpdatePermissionDto;
import com.licode.prodigoerp.auth.adapter.input.rest.mapper.AuthoritiesWebMapper;
import com.licode.prodigoerp.auth.application.port.input.PermissionCatalogUseCase;
import com.licode.prodigoerp.auth.application.port.input.command.PermissionSummaryCommand;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/{version}/s/admin/permissions")
@PreAuthorize("hasRole('SUPER_ADMIN')")
@RequiredArgsConstructor
public class PlatformPermissionController {

    private final AuthoritiesWebMapper mapper;
    private final PermissionCatalogUseCase useCase;

    @GetMapping(version = "1.0")
    public ResponseEntity<List<PermissionSummaryDto>> list(){
        return ResponseEntity.ok(mapper.toPermissionSummaryDtos(useCase.listPermissions()));
    }

    @GetMapping(value = "/{permissionId}", version = "1.0")
    public ResponseEntity<PermissionSummaryDto> get(@PathVariable UUID permissionId){
        return ResponseEntity.ok(mapper.toPermissionSummaryDto(useCase.getPermissionById(permissionId)));
    }

    @PostMapping(version = "1.0")
    public ResponseEntity<PermissionSummaryDto> create(@Valid @RequestBody CreatePermissionDto dto){
        PermissionSummaryCommand created = useCase.create(mapper.toCreatePermissionCommand(dto));

        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toPermissionSummaryDto(created));
    }

    @PutMapping(value = "/{permissionId}", version = "1.0")
    public ResponseEntity<PermissionSummaryDto> update(@PathVariable UUID permissionId, @Valid @RequestBody UpdatePermissionDto dto){
        return ResponseEntity.ok().body(
                mapper.toPermissionSummaryDto(useCase.update(permissionId, mapper.toUpdatePermissionCommand(dto)))
        );
    }

    @DeleteMapping(value = "/{permissionId}", version = "1.0")
    public ResponseEntity<Void> delete(@PathVariable UUID permissionId){
        useCase.delete(permissionId);

        return ResponseEntity.noContent().build();
    }
}
