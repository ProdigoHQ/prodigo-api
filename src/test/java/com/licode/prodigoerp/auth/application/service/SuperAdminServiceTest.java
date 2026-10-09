package com.licode.prodigoerp.auth.application.service;

import com.licode.prodigoerp.auth.application.port.input.command.*;
import com.licode.prodigoerp.auth.application.port.input.internal.SaveUserUseCase;
import com.licode.prodigoerp.auth.application.port.output.LoadUserPort;
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
class SuperAdminServiceTest {

    @Mock private LoadUserPort loadUserPort;
    @Mock private AccessProvisioningService provisioningService;
    @Mock private CurrentUserPort currentUser;
    @Mock private SaveUserUseCase saveUser;

    private SuperAdminService superAdminService;

    private RegisterSuperAdminCommand registerSuperAdminInfos;
    private User sampleUser;
    private CreateUserCommand sampleCreateUserInfo;

    private static final UUID userId = UUID.fromString("11111111-1111-1111-1111-111111111111");
    private static final String author = "PRODIGO_ERP_API";


    @BeforeEach
    void setUp() {
        superAdminService = new SuperAdminService(
                loadUserPort,
                saveUser,
                provisioningService,
                currentUser
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

        sampleCreateUserInfo = new CreateUserCommand(
                registerSuperAdminInfos.username(),
                null,
                registerSuperAdminInfos.email(),
                registerSuperAdminInfos.password(),
                registerSuperAdminInfos.firstName(),
                registerSuperAdminInfos.lastName(),
                true
        );
    }

    @Nested
    @DisplayName("Testing the Registration of a superAdmin")
    class RegistrationTests {

        @Test
        void shouldSuccessfullyRegisterSuperAdminPath1() {
            when(loadUserPort.findUserByUsername(registerSuperAdminInfos.username())).thenReturn(Optional.empty());
            when(loadUserPort.findUserByEmail(registerSuperAdminInfos.email())).thenReturn(Optional.empty());
            when(currentUser.usernameOrSystem()).thenReturn(author);
            when(saveUser.save(sampleCreateUserInfo, author)).thenReturn(sampleUser);
            when(provisioningService.ensureSuperAdminRole(author)).thenReturn(UUID.fromString("11111111-1111-1111-1111-111111111111"));


            String actual =  superAdminService.register(registerSuperAdminInfos);


            verify(loadUserPort).findUserByUsername(registerSuperAdminInfos.username());
            verify(loadUserPort).findUserByEmail(registerSuperAdminInfos.email());
            assertNotNull(actual);
            assertNotNull(loadUserPort.findUserByUsername(registerSuperAdminInfos.username()));


        }

        @Test
        void shouldSuccessfullyRegisterSuperAdminPath2() {
            when(loadUserPort.findUserByUsername(registerSuperAdminInfos.username())).thenReturn(Optional.empty());
            when(loadUserPort.findUserByEmail(registerSuperAdminInfos.email())).thenReturn(Optional.empty());
            when(currentUser.usernameOrSystem()).thenReturn(author);
            when(saveUser.save(sampleCreateUserInfo, author)).thenReturn(sampleUser);
            when(provisioningService.ensureSuperAdminRole(author)).thenReturn(UUID.fromString("11111111-1111-1111-1111-111111111111"));


            String actual =  superAdminService.register(registerSuperAdminInfos);

            verify(loadUserPort).findUserByUsername(registerSuperAdminInfos.username());
            verify(loadUserPort).findUserByEmail(registerSuperAdminInfos.email());
            verify(saveUser).save(sampleCreateUserInfo, author);
            assertNotNull(actual);
            assertNull(saveUser.save(sampleCreateUserInfo, author).getTenant());
            assertNotNull(loadUserPort.findUserByUsername(registerSuperAdminInfos.username()));

        }

        @Test
        void shouldThrowConflictExceptionWhenUsernameAlreadyExists(){

            when(loadUserPort.findUserByUsername(registerSuperAdminInfos.username())).thenReturn(Optional.of(sampleUser));

            ConflictException ex = assertThrows(ConflictException.class,
                    () -> superAdminService.register(registerSuperAdminInfos));

            assertEquals("Username already exists, please create another username", ex.getMessage());
            assertNotNull(loadUserPort.findUserByUsername(registerSuperAdminInfos.username()));
        }

        @Test
        void shouldThrowConflictExceptionWhenEmailAlreadyExists(){
            when(loadUserPort.findUserByUsername(registerSuperAdminInfos.username())).thenReturn(Optional.empty());
            when(loadUserPort.findUserByEmail(registerSuperAdminInfos.email())).thenReturn(Optional.of(sampleUser));

            ConflictException ex = assertThrows(ConflictException.class,
                    () -> superAdminService.register(registerSuperAdminInfos));

            assertEquals("Email already exists,  please try another email", ex.getMessage());
        }

    }

}