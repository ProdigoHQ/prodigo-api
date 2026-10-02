package com.licode.prodigoerp.auth.application.service;

import com.licode.prodigoerp.auth.application.port.input.RegisterSuperAdminUseCase;
import com.licode.prodigoerp.auth.application.port.input.SaveAuthoritiesUseCase;
import com.licode.prodigoerp.auth.application.port.input.SaveUserUseCase;
import com.licode.prodigoerp.auth.application.port.input.command.*;
import com.licode.prodigoerp.auth.application.port.output.LoadUserPort;
import com.licode.prodigoerp.auth.application.port.output.RoleQueryPort;
import com.licode.prodigoerp.auth.domain.model.Permission;
import com.licode.prodigoerp.auth.domain.model.Role;
import com.licode.prodigoerp.auth.domain.model.User;
import com.licode.prodigoerp.common.exception.ConflictException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class SuperAdminService implements RegisterSuperAdminUseCase {

    private final LoadUserPort loadUserPort;
    private final SaveUserUseCase saveUserUseCase;
    private final RoleQueryPort roleQueryPort;
    private final SaveAuthoritiesUseCase saveAuthoritiesUseCase;

    @Override
    @Transactional
    public String register(RegisterSuperAdminCommand registerSuperAdminCommand) {

        // check if the email, username already exist in the db
        if(loadUserPort.findUserByUsername(registerSuperAdminCommand.username()).isPresent()){
            log.error("Username {} already exists", registerSuperAdminCommand.username());
            throw new ConflictException("Username already exists, please create another username");
        }

        if(loadUserPort.findUserByEmail(registerSuperAdminCommand.email()).isPresent() ) {
            log.error("Email {} already exists", registerSuperAdminCommand.email());
            throw new ConflictException("Email already exists,  please try another email");
        };

        // TODO: need to get the username of the person connected
        String author = "PRODIGO_ERP_API";

        User fetchedUser = saveUserUseCase.save(
                new CreateUserCommand(
                        registerSuperAdminCommand.username(),
                        null,
                        registerSuperAdminCommand.email(),
                        registerSuperAdminCommand.password(),
                        registerSuperAdminCommand.firstName(),
                        registerSuperAdminCommand.lastName(),
                        true
                ),
                author
        );

        // We need to create/assigne the default super admin role and permissions
        String defaultRoleName = "SUPER_ADMIN";
        String defaultPermissionCode = "ERP.SYSTEM.READ";

        // need to fetch if the default role already exist
        Optional<Role> fetchedRole = roleQueryPort.findRoleByNameWithTenantNull(defaultRoleName);

        // get the role if already exist
        // orElse create  the default role
        Role defaultRole = fetchedRole.orElseGet(() -> saveAuthoritiesUseCase.saveRole(
                new CreateRoleCommand(
                        defaultRoleName,
                        null,
                        "SUPER_ADMIN : The Default role to access the ERP System Dashboard",
                        true,
                        author
                )
        ));

        // Assign the role to the user
        saveAuthoritiesUseCase.assignedRoleToUser(
                new AssignRoleCommand(
                        fetchedUser.getId(),
                        defaultRole.getId(),
                        null,
                        author
                )
        );

        // Then we fetched/create the default permission
        Optional<Permission> fetchedPermission = roleQueryPort.findPermissionByCode(defaultPermissionCode);

        Permission defaultPermission = fetchedPermission.orElseGet(() -> saveAuthoritiesUseCase.savePermission(
                new CreatePermissionCommand(
                        "READ-Only Dashboard: The Default permission that determine if a user Super Admin",
                        null,
                        "READ",
                        "SYSTEM"
                ),
                author
        ));

        // Assign the permission to the user
        saveAuthoritiesUseCase.assignedPermissionToRole(
                defaultPermission.getId(),
                new AssignRoleCommand(
                        fetchedUser.getId(),
                        defaultRole.getId(),
                        null,
                        author
                )
        );

        return "The super admin was created successfully with default permission: " + defaultPermission.getCode();
    }
}
