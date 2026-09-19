package com.licode.prodigoerp.auth.application.service;

import com.licode.prodigoerp.auth.application.port.input.SaveAuthoritiesUseCase;
import com.licode.prodigoerp.auth.application.port.input.SaveUserUseCase;
import com.licode.prodigoerp.auth.application.port.input.command.*;
import com.licode.prodigoerp.auth.application.port.output.LoadUserPort;
import com.licode.prodigoerp.auth.application.port.output.RoleQueryPort;
import com.licode.prodigoerp.auth.domain.model.Permission;
import com.licode.prodigoerp.auth.domain.model.Role;
import com.licode.prodigoerp.auth.domain.model.User;
import com.licode.prodigoerp.common.exception.ConflictException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class SuperAdminServiceTest {

    @Mock private LoadUserPort loadUserPort;
    @Mock private SaveUserUseCase saveUserUseCase;
    @Mock private RoleQueryPort roleQueryPort;
    @Mock private SaveAuthoritiesUseCase saveAuthoritiesUseCase;

    private SuperAdminService superAdminService;

    private RegisterSuperAdminCommand registerSuperAdminInfos;
    private User sampleUser;
    private Role sampleRole;
    private Permission samplePermission;
    private CreateUserCommand sampleCreateUserInfo;
    private CreateRoleCommand sampleCreateRole;
    private AssignRoleCommand sampleAssignRole;
    private CreatePermissionCommand sampleCreatePermission;

    private static final UUID userId = UUID.fromString("11111111-1111-1111-1111-111111111111");
    private static final UUID roleId = UUID.fromString("11111111-1111-1111-1111-111111111111");
    private static final UUID permissionId = UUID.fromString("11111111-1111-1111-1111-111111111111");


    private static final String author = "PRODIGO_ERP_API";
    private static final String defaultRoleName = "SUPER_ADMIN";
    private static final String defaultPermissionCode = "ERP.SYSTEM.READ";


    @BeforeEach
    void setUp() {
        superAdminService = new SuperAdminService(
                loadUserPort,
                saveUserUseCase,
                roleQueryPort,
                saveAuthoritiesUseCase
        );

        registerSuperAdminInfos = new RegisterSuperAdminCommand(
                "superadmin",
                "superadmin@example.com",
                "SecureP@ssw0rd!",
                "John",
                "Doe"
        );

        sampleUser = new User();
        sampleUser.setId(userId);

        sampleRole = new Role();
        sampleRole.setId(roleId);

        samplePermission = new Permission();
        samplePermission.setId(permissionId);

        sampleCreateUserInfo = new CreateUserCommand(
                registerSuperAdminInfos.username(),
                null,
                registerSuperAdminInfos.email(),
                registerSuperAdminInfos.password(),
                registerSuperAdminInfos.firstName(),
                registerSuperAdminInfos.lastName(),
                true
        );

        sampleCreateRole = new CreateRoleCommand(
                defaultRoleName,
                null,
                "SUPER_ADMIN : The Default role to access the ERP System Dashboard",
                true,
                author
        );

        sampleAssignRole = new AssignRoleCommand(
                sampleUser.getId(),
                sampleRole.getId(),
                null,
                author
        );

        sampleCreatePermission = new CreatePermissionCommand(
                "READ-Only Dashboard: The Default permission that determine if a user Super Admin",
                null,
                "READ",
                "SYSTEM"
        );
    }

    @Nested
    @DisplayName("Testing the Registration of a superAdmin")
    class RegistrationTests {

        @Test
        void shouldSuccessfullyRegisterSuperAdminPath1() {
            when(loadUserPort.findUserByUsername(registerSuperAdminInfos.username())).thenReturn(Optional.empty());
            when(loadUserPort.findUserByEmail(registerSuperAdminInfos.email())).thenReturn(Optional.empty());
            when(saveUserUseCase.save(sampleCreateUserInfo, author)).thenReturn(sampleUser);
            when(roleQueryPort.findRoleByNameWithTenantNull(defaultRoleName)).thenReturn(Optional.of(sampleRole));
//            when(saveAuthoritiesUseCase.saveRole(sampleCreateRole)).thenReturn(sampleRole);
            when(roleQueryPort.findPermissionByCode(defaultPermissionCode)).thenReturn(Optional.of(samplePermission));
//            when(saveAuthoritiesUseCase.savePermission(sampleCreatePermission, author)).thenReturn(samplePermission);

            String actual =  superAdminService.register(registerSuperAdminInfos);

//            saveAuthoritiesUseCase.assignedRoleToUser(sampleAssignRole);
//            saveAuthoritiesUseCase.assignedPermissionToRole(samplePermission.getId(),sampleAssignRole);


            verify(loadUserPort).findUserByUsername(registerSuperAdminInfos.username());
            verify(loadUserPort).findUserByEmail(registerSuperAdminInfos.email());
            verify(saveUserUseCase).save(sampleCreateUserInfo, author);
            verify(roleQueryPort).findRoleByNameWithTenantNull(defaultRoleName);
            verify(saveAuthoritiesUseCase, times(0)).saveRole(sampleCreateRole);
            verify(saveAuthoritiesUseCase).assignedRoleToUser(sampleAssignRole);
            verify(roleQueryPort).findPermissionByCode(defaultPermissionCode);
            verify(saveAuthoritiesUseCase, times(0)).savePermission(sampleCreatePermission, author);
            assertNotNull(actual);
            assertNotNull(loadUserPort.findUserByUsername(registerSuperAdminInfos.username()));


        }

        @Test
        void shouldSuccessfullyRegisterSuperAdminPath2() {
            when(loadUserPort.findUserByUsername(registerSuperAdminInfos.username())).thenReturn(Optional.empty());
            when(loadUserPort.findUserByEmail(registerSuperAdminInfos.email())).thenReturn(Optional.empty());
            when(saveUserUseCase.save(sampleCreateUserInfo, author)).thenReturn(sampleUser);
            when(roleQueryPort.findRoleByNameWithTenantNull(defaultRoleName)).thenReturn(Optional.empty());
            when(saveAuthoritiesUseCase.saveRole(sampleCreateRole)).thenReturn(sampleRole);
            when(roleQueryPort.findPermissionByCode(defaultPermissionCode)).thenReturn(Optional.empty());
            when(saveAuthoritiesUseCase.savePermission(sampleCreatePermission, author)).thenReturn(samplePermission);

            String actual =  superAdminService.register(registerSuperAdminInfos);

//            saveAuthoritiesUseCase.assignedRoleToUser(sampleAssignRole);
//            saveAuthoritiesUseCase.assignedPermissionToRole(samplePermission.getId(),sampleAssignRole);

            verify(loadUserPort).findUserByUsername(registerSuperAdminInfos.username());
            verify(loadUserPort).findUserByEmail(registerSuperAdminInfos.email());
            verify(saveUserUseCase).save(sampleCreateUserInfo, author);
            verify(roleQueryPort).findRoleByNameWithTenantNull(defaultRoleName);
            verify(saveAuthoritiesUseCase).saveRole(sampleCreateRole);
            verify(saveAuthoritiesUseCase).assignedRoleToUser(sampleAssignRole);
            verify(roleQueryPort).findPermissionByCode(defaultPermissionCode);
            verify(saveAuthoritiesUseCase).savePermission(sampleCreatePermission, author);
            assertNotNull(actual);
            assertNull(saveUserUseCase.save(sampleCreateUserInfo, author).getTenant());
            assertNotNull(loadUserPort.findUserByUsername(registerSuperAdminInfos.username()));

        }

        @Test
        void shouldThrowConflictExceptionWhenUsernameAlreadyExists(){

            when(loadUserPort.findUserByUsername(registerSuperAdminInfos.username())).thenReturn(Optional.of(sampleUser));

            ConflictException ex = assertThrows(ConflictException.class,
                    () -> superAdminService.register(registerSuperAdminInfos));

            assertEquals("Username already exists", ex.getMessage());
            assertNotNull(loadUserPort.findUserByUsername(registerSuperAdminInfos.username()));
        }

        @Test
        void shouldThrowConflictExceptionWhenEmailAlreadyExists(){
            when(loadUserPort.findUserByUsername(registerSuperAdminInfos.username())).thenReturn(Optional.empty());
            when(loadUserPort.findUserByEmail(registerSuperAdminInfos.email())).thenReturn(Optional.of(sampleUser));

            ConflictException ex = assertThrows(ConflictException.class,
                    () -> superAdminService.register(registerSuperAdminInfos));

            assertEquals("Email already exists", ex.getMessage());
        }

    }

}