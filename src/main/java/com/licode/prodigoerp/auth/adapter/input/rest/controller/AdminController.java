package com.licode.prodigoerp.auth.adapter.input.rest.controller;

import com.licode.prodigoerp.auth.adapter.input.rest.dto.CreateSuperAdminDto;
import com.licode.prodigoerp.auth.adapter.input.rest.mapper.AuthWebMapper;
import com.licode.prodigoerp.auth.application.port.input.RegisterSuperAdminUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// NOTE: when the authorities are being build, the prefix "ROLE_" is added to the roles
// And the prefix "PERM_" for the permissions
// Example: to check for a role -> hasRole('roleName') the functions automatically add "ROLE_"
// Example: to check for permissions -> hasAuthority("PERM_permissionCode") must start with "PERM_"
// Note you can also verify hasAuthorities("ROLE_SUPER_ADMIN", "ERP.ADMIN.CREATE")
@RestController
@PreAuthorize("hasRole('SUPER_ADMIN')")
@RequestMapping("/api/{version}/s/admin")
@RequiredArgsConstructor
public class AdminController {

    private final RegisterSuperAdminUseCase registerSuperAdminUseCase;
    private final AuthWebMapper authWebMapper;

    @PreAuthorize("hasAuthority('ERP.ADMIN.CREATE')")
    @PostMapping(value = "/create", version = "1.0")
    public ResponseEntity<String> createSuperAdmin(@Valid @RequestBody CreateSuperAdminDto createSuperAdminDto) {
        return ResponseEntity.ok().body(
                registerSuperAdminUseCase.register(authWebMapper.toRegisterSuperAdminCommand(createSuperAdminDto))
        );
    }
}
