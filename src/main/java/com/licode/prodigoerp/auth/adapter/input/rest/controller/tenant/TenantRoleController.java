package com.licode.prodigoerp.auth.adapter.input.rest.controller.tenant;

import com.licode.prodigoerp.auth.adapter.input.rest.dto.RoleSummaryDto;
import com.licode.prodigoerp.auth.adapter.input.rest.dto.SaveRoleDto;
import com.licode.prodigoerp.auth.adapter.input.rest.mapper.AuthoritiesWebMapper;
import com.licode.prodigoerp.auth.application.port.input.TenantRoleUseCase;
import com.licode.prodigoerp.auth.application.port.input.command.RoleSummaryCommand;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/{version}/roles")
@RequiredArgsConstructor
public class TenantRoleController {

    private final TenantRoleUseCase useCase;
    private final AuthoritiesWebMapper mapper;

//    @PreAuthorize("hasAuthority('AUTH.ROLE.READ')")
    @GetMapping(version = "1.0")
    public ResponseEntity<List<RoleSummaryDto>> list(){
        return ResponseEntity.ok().body(
                mapper.toRoleSummaryDtos(useCase.list())
        );
    }

//    @PreAuthorize("hasAuthority('AUTH.ROLE.READ')")
    @GetMapping(value = "/{roleId}", version = "1.0")
    public ResponseEntity<RoleSummaryDto> get(@PathVariable UUID roleId) {
        return ResponseEntity.ok().body(mapper.toRoleSummaryDto(useCase.get(roleId)));
    }

//    @PreAuthorize("hasAuthority('AUTH.ROLE.CREATE')")
    @PostMapping(version = "1.0")
    public ResponseEntity<RoleSummaryDto> create(@Valid @RequestBody SaveRoleDto dto){
        RoleSummaryCommand created = useCase.create(mapper.toSaveRoleCommand(dto));

        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toRoleSummaryDto(created));
    }

//    @PreAuthorize("hasAuthority('AUTH.ROLE.UPDATE')")
    @PutMapping(value = "/{roleId}", version = "1.0")
    public ResponseEntity<RoleSummaryDto> update(@PathVariable UUID roleId, @Valid @RequestBody SaveRoleDto dto){
        RoleSummaryCommand updated = useCase.update(roleId, mapper.toSaveRoleCommand(dto));

        return ResponseEntity.ok().body(mapper.toRoleSummaryDto(updated));
    }

//    @PreAuthorize("hasAuthority('AUTH.ROLE.DELETE')")
    @DeleteMapping(value = "/{roleId}", version = "1.0")
    public ResponseEntity<Void> delete(@PathVariable UUID roleId) {
        useCase.delete(roleId);
        return ResponseEntity.noContent().build();
    }
}
