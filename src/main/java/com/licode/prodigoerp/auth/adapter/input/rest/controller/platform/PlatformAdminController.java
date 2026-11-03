package com.licode.prodigoerp.auth.adapter.input.rest.controller.platform;

import com.licode.prodigoerp.auth.adapter.input.rest.dto.request.CreateAdminRequest;
import com.licode.prodigoerp.auth.adapter.input.rest.dto.request.UpdateUserRequest;
import com.licode.prodigoerp.auth.adapter.input.rest.dto.response.UserDetailsResponse;
import com.licode.prodigoerp.auth.adapter.input.rest.dto.response.UserSummaryResponse;
import com.licode.prodigoerp.auth.adapter.input.rest.mapper.AuthRestMapper;
import com.licode.prodigoerp.auth.adapter.input.rest.mapper.UserRestMapper;
import com.licode.prodigoerp.auth.application.port.input.PlatformAdminQueryUseCase;
import com.licode.prodigoerp.auth.application.port.input.RegisterPlatformAdminUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

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
    private final AuthRestMapper authRestMapper;
    private final UserRestMapper userRestMapper;

    @PreAuthorize("hasAnyAuthority('PERM_ERP.ADMIN.UPDATE', 'PERM_ERP.ADMIN.CREATE')")
    @PostMapping(value = "/create", version = "1.0")
    public ResponseEntity<String> createPlatformAdmin(@Valid @RequestBody CreateAdminRequest createAdminRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                registerPlatformAdminUseCase.register(authRestMapper.toCreateAdminCommand(createAdminRequest))
        );
    }

    @GetMapping(value = "/", version = "1.0")
    public ResponseEntity<List<UserSummaryResponse>> getAllPlatformAdmins(){

        return ResponseEntity.ok().body(platformAdminQuery.findAllAdmins().stream().map(
                userRestMapper::toUserSummaryResponse
        ).toList());
    }

    @GetMapping(value = "/{adminId}", version = "1.0")
    public ResponseEntity<UserDetailsResponse> getPlatformAdmin(@PathVariable UUID adminId){

        return ResponseEntity.ok().body(userRestMapper.toUserDetailsResponse(platformAdminQuery.findUserById(adminId)));
    }

    @PreAuthorize("hasAnyAuthority('PERM_ERP.ADMIN.UPDATE', 'PERM_ERP.ADMIN.CREATE')")
    @PutMapping(value = "/{adminId}", version = "1.0")
    public ResponseEntity<String> updatePlatformAdmin(@PathVariable UUID adminId, @Valid @RequestBody UpdateUserRequest updateUserRequest) {

        return ResponseEntity.status(HttpStatus.OK).body(
                registerPlatformAdminUseCase.update(adminId, userRestMapper.toUpdatedUserCommand(updateUserRequest))
        );
    }
}
