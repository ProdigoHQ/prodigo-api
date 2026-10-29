package com.licode.prodigoerp.auth.adapter.input.rest.controller.platform;

import com.licode.prodigoerp.auth.adapter.input.rest.dto.RoleSummaryDto;
import com.licode.prodigoerp.auth.adapter.input.rest.dto.SaveRoleDto;
import com.licode.prodigoerp.auth.adapter.input.rest.mapper.AuthoritiesWebMapper;
import com.licode.prodigoerp.auth.application.port.input.SystemRoleUseCase;
import com.licode.prodigoerp.auth.application.port.input.command.RoleSummaryCommand;
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
@PreAuthorize("hasRole('ERP.ROLE.READ')")
@RequiredArgsConstructor
public class PlatformRoleController {

    private final AuthoritiesWebMapper  mapper;
    private final SystemRoleUseCase useCase;

    // Get system roles
    @GetMapping(value = "/roles", version = "1.0")
    public ResponseEntity<List<RoleSummaryDto>> list(){
        return ResponseEntity.ok().body(
                mapper.toRoleSummaryDtos(useCase.list())
        );
    }

    @GetMapping(value = "/roles/{roleId}", version = "1.0")
    public ResponseEntity<RoleSummaryDto> get(@PathVariable UUID roleId){
        return ResponseEntity.ok().body(mapper.toRoleSummaryDto(useCase.get(roleId)));
    }

    @PreAuthorize("hasAnyAuthority('ERP.ROLE.CREATE','ERP.ROLE.UPDATE')")
    @PostMapping(value = "/roles", version = "1.0")
    public ResponseEntity<RoleSummaryDto> create(@Valid @RequestBody SaveRoleDto dto){
        RoleSummaryCommand created = useCase.create(mapper.toSaveRoleCommand(dto));

        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toRoleSummaryDto(created));
    }

    @PreAuthorize("hasAnyAuthority('ERP.ROLE.CREATE','ERP.ROLE.UPDATE')")
    @PutMapping(value = "/roles/{roleId}", version = "1.0")
    public ResponseEntity<RoleSummaryDto> update(@PathVariable UUID roleId, @Valid @RequestBody SaveRoleDto dto){

        return ResponseEntity.ok().body(mapper.toRoleSummaryDto(useCase.update(roleId, mapper.toSaveRoleCommand(dto))));
    }

    @PreAuthorize("hasAuthority('ERP.ROLE.DELETE')")
    @DeleteMapping(value = "/roles/{roleId}", version = "1.0")
    public ResponseEntity<Void> delete(@PathVariable UUID roleId){
        useCase.delete(roleId);

        return ResponseEntity.noContent().build();
    }

    // read-only : superadmin can inspect tenant's roles
    @PreAuthorize("hasAuthority('ERP.USER.READ')")
    @GetMapping(value = "/tenants/{tenantId}/roles", version = "1.0")
    public ResponseEntity<List<RoleSummaryDto>> listTenantRoles(@PathVariable UUID tenantId){
        return ResponseEntity.ok().body(mapper.toRoleSummaryDtos(useCase.listTenantRoles(tenantId)));
    }

    @PreAuthorize("hasAuthority('ERP.USER.READ')")
    @GetMapping(value = "/tenants/{tenantId}/roles/{roleId}", version = "1.0")
    public ResponseEntity<RoleSummaryDto> get(@PathVariable UUID tenantId, @PathVariable UUID roleId){

        return ResponseEntity.ok().body(mapper.toRoleSummaryDto(useCase.getTenantRole(tenantId, roleId)));
    }
}
