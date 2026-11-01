package com.licode.prodigoerp.auth.adapter.input.rest.controller.tenant;

import com.licode.prodigoerp.auth.adapter.input.rest.dto.request.SaveRoleRequest;
import com.licode.prodigoerp.auth.adapter.input.rest.dto.response.RoleDetailsResponse;
import com.licode.prodigoerp.auth.adapter.input.rest.mapper.AuthoritiesRestMapper;
import com.licode.prodigoerp.auth.application.port.input.TenantRoleUseCase;
import com.licode.prodigoerp.auth.application.port.input.query.GetRoleDetailsQuery;
import com.licode.prodigoerp.auth.application.port.input.result.RoleDetailsResult;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/{version}/roles")
@PreAuthorize("hasAuthority('PERM_TENANT.ROLE.READ')")
@RequiredArgsConstructor
public class TenantRoleController {

    private final TenantRoleUseCase useCase;
    private final AuthoritiesRestMapper mapper;

    @GetMapping(version = "1.0")
    public ResponseEntity<List<RoleDetailsResponse>> list(){
        return ResponseEntity.ok().body(
                mapper.toRoleDetailsResponses(useCase.list())
        );
    }

    @GetMapping(value = "/{roleId}", version = "1.0")
    public ResponseEntity<RoleDetailsResponse> get(@PathVariable UUID roleId) {
        return ResponseEntity.ok().body(
                mapper.toRoleDetailsResponse(useCase.get(new GetRoleDetailsQuery(roleId))));
    }

    @PreAuthorize("hasAnyAuthority('PERM_TENANT.ROLE.CREATE', 'PERM_TENANT.ROLE.UPDATE')")
    @PostMapping(version = "1.0")
    public ResponseEntity<RoleDetailsResponse> create(@Valid @RequestBody SaveRoleRequest request){
        RoleDetailsResult created = useCase.create(mapper.toSaveRoleCommand(request));

        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toRoleDetailsResponse(created));
    }

    @PreAuthorize("hasAnyAuthority('PERM_TENANT.ROLE.CREATE', 'PERM_TENANT.ROLE.UPDATE')")
    @PutMapping(value = "/{roleId}", version = "1.0")
    public ResponseEntity<RoleDetailsResponse> update(@PathVariable UUID roleId, @Valid @RequestBody SaveRoleRequest request){
        RoleDetailsResult updated = useCase.update(roleId, mapper.toSaveRoleCommand(request));

        return ResponseEntity.ok().body(mapper.toRoleDetailsResponse(updated));
    }

    @PreAuthorize("hasAuthority('PERM_TENANT.ROLE.DELETE')")
    @DeleteMapping(value = "/{roleId}", version = "1.0")
    public ResponseEntity<Void> delete(@PathVariable UUID roleId) {
        useCase.delete(roleId);
        return ResponseEntity.noContent().build();
    }
}
