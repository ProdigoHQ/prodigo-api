package com.licode.prodigoerp.auth.adapter.input.rest.controller.platform;

import com.licode.prodigoerp.auth.adapter.input.rest.dto.request.CreatePermissionRequest;
import com.licode.prodigoerp.auth.adapter.input.rest.dto.request.UpdatePermissionRequest;
import com.licode.prodigoerp.auth.adapter.input.rest.dto.response.PermissionSummaryResponse;
import com.licode.prodigoerp.auth.adapter.input.rest.mapper.AuthoritiesRestMapper;
import com.licode.prodigoerp.auth.application.port.input.PermissionCatalogUseCase;
import com.licode.prodigoerp.auth.application.port.input.query.GetPermissionSummaryQuery;
import com.licode.prodigoerp.auth.application.port.input.result.PermissionSummaryResult;
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
@PreAuthorize("hasAuthority('PERM_ERP.PERMISSION.READ')")
@RequiredArgsConstructor
public class PlatformPermissionController {

    private final AuthoritiesRestMapper mapper;
    private final PermissionCatalogUseCase useCase;

    @GetMapping(version = "1.0")
    public ResponseEntity<List<PermissionSummaryResponse>> list(){
        return ResponseEntity.ok(mapper.toPermissionSummaryResponses(useCase.listPermissions()));
    }

    @GetMapping(value = "/{permissionId}", version = "1.0")
    public ResponseEntity<PermissionSummaryResponse> get(@PathVariable UUID permissionId){
        return ResponseEntity.ok(mapper.toPermissionSummaryResponse(
                useCase.getPermissionById(new GetPermissionSummaryQuery(permissionId))));
    }

    @PreAuthorize("hasAnyAuthority('PERM_ERP.PERMISSION.CREATE','PERM_ERP.PERMISSION.UPDATE')")
    @PostMapping(version = "1.0")
    public ResponseEntity<PermissionSummaryResponse> create(@Valid @RequestBody CreatePermissionRequest request){
        PermissionSummaryResult created = useCase.create(mapper.toCreatePermissionCommand(request));

        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toPermissionSummaryResponse(created));
    }

    @PreAuthorize("hasAnyAuthority('PERM_ERP.PERMISSION.CREATE','PERM_ERP.PERMISSION.UPDATE')")
    @PutMapping(value = "/{permissionId}", version = "1.0")
    public ResponseEntity<PermissionSummaryResponse> update(@PathVariable UUID permissionId, @Valid @RequestBody UpdatePermissionRequest request){
        return ResponseEntity.ok().body(
                mapper.toPermissionSummaryResponse(useCase.update(permissionId, mapper.toUpdatePermissionCommand(request)))
        );
    }

    @PreAuthorize("hasAuthority('PERM_ERP.PERMISSION.DELETE')")
    @DeleteMapping(value = "/{permissionId}", version = "1.0")
    public ResponseEntity<Void> delete(@PathVariable UUID permissionId){
        useCase.delete(permissionId);

        return ResponseEntity.noContent().build();
    }
}
