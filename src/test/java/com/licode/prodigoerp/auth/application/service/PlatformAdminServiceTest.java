package com.licode.prodigoerp.auth.application.service;

import com.licode.prodigoerp.auth.application.port.input.command.*;
import com.licode.prodigoerp.auth.application.port.input.internal.SaveUserUseCase;
import com.licode.prodigoerp.auth.application.port.output.LoadUserPort;
import com.licode.prodigoerp.auth.application.port.output.RolePersistencePort;
import com.licode.prodigoerp.auth.application.service.platform.PlatformAdminService;
import com.licode.prodigoerp.auth.domain.model.User;
import com.licode.prodigoerp.common.exception.ConflictException;
import com.licode.prodigoerp.common.shared.application.output.CurrentUserPort;
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
class PlatformAdminServiceTest {

    @Mock private LoadUserPort loadUserPort;
    @Mock private AccessProvisioningService provisioningService;
    @Mock private CurrentUserPort currentUser;
    @Mock private SaveUserUseCase saveUser;
    @Mock private RolePersistencePort rolePort;

    private PlatformAdminService platformAdminService;

    private CreateAdminCommand createAdminCommand;
    private User sampleUser;
    private CreateUserCommand sampleCreateUserInfo;

    private static final UUID userId = UUID.fromString("11111111-1111-1111-1111-111111111111");
    private static final UUID roleId = UUID.fromString("11111111-1111-1111-1111-111111111111");
    private static final String author = "PRODIGO_ERP_API";


    @BeforeEach
    void setUp() {
        platformAdminService = new PlatformAdminService(
                loadUserPort,
                saveUser,
                provisioningService,
                currentUser,
                rolePort
        );

        createAdminCommand = new CreateAdminCommand(
                "superadmin",
                "superadmin@example.com",
                "SecureP@ssw0rd!",
                "John",
                "Doe",
                roleId
        );

        sampleUser = new User();
        sampleUser.setId(userId);

        sampleCreateUserInfo = new CreateUserCommand(
                createAdminCommand.username(),
                null,
                createAdminCommand.email(),
                createAdminCommand.password(),
                createAdminCommand.firstName(),
                createAdminCommand.lastName(),
                true
        );
    }

    @Nested
    @DisplayName("Testing the Registration of a superAdmin")
    class RegistrationTests {

        @Test
        void shouldSuccessfullyRegisterSuperAdminPath1() {
            when(loadUserPort.findUserByUsername(createAdminCommand.username())).thenReturn(Optional.empty());
            when(loadUserPort.findUserByEmail(createAdminCommand.email())).thenReturn(Optional.empty());
            when(currentUser.usernameOrSystem()).thenReturn(author);
            when(saveUser.save(sampleCreateUserInfo, author)).thenReturn(sampleUser);
//            when(provisioningService.ensureSuperAdminRole(author)).thenReturn(UUID.fromString("11111111-1111-1111-1111-111111111111"));


            String actual =  platformAdminService.register(createAdminCommand);


            verify(loadUserPort).findUserByUsername(createAdminCommand.username());
            verify(loadUserPort).findUserByEmail(createAdminCommand.email());
            assertNotNull(actual);
            assertNotNull(loadUserPort.findUserByUsername(createAdminCommand.username()));


        }

        @Test
        void shouldSuccessfullyRegisterSuperAdminPath2() {
            when(loadUserPort.findUserByUsername(createAdminCommand.username())).thenReturn(Optional.empty());
            when(loadUserPort.findUserByEmail(createAdminCommand.email())).thenReturn(Optional.empty());
            when(currentUser.usernameOrSystem()).thenReturn(author);
            when(saveUser.save(sampleCreateUserInfo, author)).thenReturn(sampleUser);
//            when(provisioningService.ensureSuperAdminRole(author)).thenReturn(UUID.fromString("11111111-1111-1111-1111-111111111111"));


            String actual =  platformAdminService.register(createAdminCommand);

            verify(loadUserPort).findUserByUsername(createAdminCommand.username());
            verify(loadUserPort).findUserByEmail(createAdminCommand.email());
            verify(saveUser).save(sampleCreateUserInfo, author);
            assertNotNull(actual);
            assertNull(saveUser.save(sampleCreateUserInfo, author).getTenant());
            assertNotNull(loadUserPort.findUserByUsername(createAdminCommand.username()));

        }

        @Test
        void shouldThrowConflictExceptionWhenUsernameAlreadyExists(){

            when(loadUserPort.findUserByUsername(createAdminCommand.username())).thenReturn(Optional.of(sampleUser));

            ConflictException ex = assertThrows(ConflictException.class,
                    () -> platformAdminService.register(createAdminCommand));

            assertEquals("Username already exists, please create another username", ex.getMessage());
            assertNotNull(loadUserPort.findUserByUsername(createAdminCommand.username()));
        }

        @Test
        void shouldThrowConflictExceptionWhenEmailAlreadyExists(){
            when(loadUserPort.findUserByUsername(createAdminCommand.username())).thenReturn(Optional.empty());
            when(loadUserPort.findUserByEmail(createAdminCommand.email())).thenReturn(Optional.of(sampleUser));

            ConflictException ex = assertThrows(ConflictException.class,
                    () -> platformAdminService.register(createAdminCommand));

            assertEquals("Email already exists,  please try another email", ex.getMessage());
        }

    }

    @Nested
    @DisplayName("Testing the Login of a superAdmin")
    class LoginTests {


    }

}
