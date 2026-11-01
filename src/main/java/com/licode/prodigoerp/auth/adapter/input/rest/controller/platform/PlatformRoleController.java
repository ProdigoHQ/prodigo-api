package com.licode.prodigoerp.auth.adapter.input.rest.controller.platform;

import com.licode.prodigoerp.auth.adapter.input.rest.dto.request.SaveRoleRequest;
import com.licode.prodigoerp.auth.adapter.input.rest.dto.response.RoleDetailsResponse;
import com.licode.prodigoerp.auth.adapter.input.rest.mapper.AuthoritiesRestMapper;
import com.licode.prodigoerp.auth.application.port.input.SystemRoleUseCase;
import com.licode.prodigoerp.auth.application.port.input.query.GetRoleDetailsQuery;
import com.licode.prodigoerp.auth.application.port.input.query.GetTenantRoleDetailsQuery;
import com.licode.prodigoerp.auth.application.port.input.query.ListTenantRolesQuery;
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
@RequestMapping("/api/{version}/s/admin")
@PreAuthorize("hasRole('PERM_ERP.ROLE.READ')")
@RequiredArgsConstructor
public class PlatformRoleController {

    private final AuthoritiesRestMapper  mapper;
    private final SystemRoleUseCase useCase;

    // Get system roles
    @GetMapping(value = "/roles", version = "1.0")
    public ResponseEntity<List<RoleDetailsResponse>> list(){
        return ResponseEntity.ok().body(
                mapper.toRoleDetailsResponses(useCase.list())
        );
    }

    @GetMapping(value = "/roles/{roleId}", version = "1.0")
    public ResponseEntity<RoleDetailsResponse> get(@PathVariable UUID roleId){
        return ResponseEntity.ok().body(
                mapper.toRoleDetailsResponse(useCase.get(new GetRoleDetailsQuery(roleId))));
    }

    @PreAuthorize("hasAnyAuthority('PERM_ERP.ROLE.CREATE','PERM_ERP.ROLE.UPDATE')")
    @PostMapping(value = "/roles", version = "1.0")
    public ResponseEntity<RoleDetailsResponse> create(@Valid @RequestBody SaveRoleRequest request){
        RoleDetailsResult created = useCase.create(mapper.toSaveRoleCommand(request));

        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toRoleDetailsResponse(created));
    }

    @PreAuthorize("hasAnyAuthority('PERM_ERP.ROLE.CREATE','PERM_ERP.ROLE.UPDATE')")
    @PutMapping(value = "/roles/{roleId}", version = "1.0")
    public ResponseEntity<RoleDetailsResponse> update(@PathVariable UUID roleId, @Valid @RequestBody SaveRoleRequest request){

        return ResponseEntity.ok().body(mapper.toRoleDetailsResponse(useCase.update(roleId, mapper.toSaveRoleCommand(request))));
    }

    @PreAuthorize("hasAuthority('PERM_ERP.ROLE.DELETE')")
    @DeleteMapping(value = "/roles/{roleId}", version = "1.0")
    public ResponseEntity<Void> delete(@PathVariable UUID roleId){
        useCase.delete(roleId);

        return ResponseEntity.noContent().build();
    }

    // read-only : superadmin can inspect tenant's roles
    @PreAuthorize("hasAuthority('PERM_ERP.USER.READ')")
    @GetMapping(value = "/tenants/{tenantId}/roles", version = "1.0")
    public ResponseEntity<List<RoleDetailsResponse>> listTenantRoles(@PathVariable UUID tenantId){
        return ResponseEntity.ok().body(
                mapper.toRoleDetailsResponses(useCase.listTenantRoles(new ListTenantRolesQuery(tenantId))));
    }

    @PreAuthorize("hasAuthority('PERM_ERP.USER.READ')")
    @GetMapping(value = "/tenants/{tenantId}/roles/{roleId}", version = "1.0")
    public ResponseEntity<RoleDetailsResponse> get(@PathVariable UUID tenantId, @PathVariable UUID roleId){

        return ResponseEntity.ok().body(mapper.toRoleDetailsResponse(
                useCase.getTenantRole(new GetTenantRoleDetailsQuery(tenantId, roleId))));
    }
}
