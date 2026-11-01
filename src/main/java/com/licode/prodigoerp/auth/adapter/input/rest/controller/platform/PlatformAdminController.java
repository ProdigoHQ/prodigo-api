package com.licode.prodigoerp.auth.adapter.input.rest.controller.platform;

import com.licode.prodigoerp.auth.adapter.input.rest.dto.CreatePlatformAdminDto;
import com.licode.prodigoerp.auth.adapter.input.rest.dto.response.UserListResponse;
import com.licode.prodigoerp.auth.adapter.input.rest.mapper.AuthWebMapper;
import com.licode.prodigoerp.auth.adapter.input.rest.mapper.UserWebMapper;
import com.licode.prodigoerp.auth.application.port.input.PlatformAdminQueryUseCase;
import com.licode.prodigoerp.auth.application.port.input.RegisterPlatformAdminUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// NOTE: when the authorities are being build, the prefix "ROLE_" is added to the roles
// And the prefix "PERM_" for the permissions
// Example: to check for a role -> hasRole('roleName') the functions automatically add "ROLE_"
// Example: to check for permissions -> hasAuthority("PERM_permissionCode") must start with "PERM_"
// Note you can also verify hasAuthorities("ROLE_SUPER_ADMIN", "ERP.ADMIN.CREATE")
@RestController
@RequestMapping("/api/{version}/s/admin")
@PreAuthorize("hasAuthority('PERM_ERP.ADMIN.READ')")
@RequiredArgsConstructor
public class PlatformAdminController {

    private final RegisterPlatformAdminUseCase registerPlatformAdminUseCase;
    private final PlatformAdminQueryUseCase platformAdminQuery;
    private final AuthWebMapper authWebMapper;
    private final UserWebMapper userWebMapper;

    @PreAuthorize("hasAnyAuthority('PERM_ERP.ADMIN.UPDATE', 'PERM_ERP.ADMIN.CREATE')")
    @PostMapping(value = "/create", version = "1.0")
    public ResponseEntity<String> createPlatformAdmin(@Valid @RequestBody CreatePlatformAdminDto createSuperAdminDto) {
        return ResponseEntity.ok().body(
                registerPlatformAdminUseCase.register(authWebMapper.toRegisterSuperAdminCommand(createSuperAdminDto))
        );
    }

    @GetMapping(value = "/admins", version = "1.0")
    public ResponseEntity<List<UserListResponse>> getAllPlatformAdmins(){

        return ResponseEntity.ok().body(platformAdminQuery.get().stream().map(
                userWebMapper::toUserListResponse
        ).toList());
    }
}
