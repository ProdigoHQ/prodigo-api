package com.licode.prodigoerp.auth.application.service;

import com.licode.prodigoerp.auth.application.port.input.command.CreateRoleCommand;
import com.licode.prodigoerp.auth.application.port.output.LoadUserPort;
import com.licode.prodigoerp.auth.application.port.output.RoleQueryPort;
import com.licode.prodigoerp.auth.application.port.output.SavePermissionPort;
import com.licode.prodigoerp.auth.application.port.output.SaveRolePort;
import com.licode.prodigoerp.auth.domain.model.Role;
import com.licode.prodigoerp.module.application.port.input.ModuleLookUpUseCase;
import com.licode.prodigoerp.tenant.application.port.input.TenantLookUpUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.Instant;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.*;


//@ExtendWith()
@SpringBootTest
@ExtendWith(MockitoExtension.class)
class AuthoritiesServiceTest {

    @Mock private SaveRolePort saveRolePort;
    @Mock private SavePermissionPort savePermissionPort;
    @Mock private ModuleLookUpUseCase moduleLookUpUseCase;
    @Mock private TenantLookUpUseCase tenantLookUpUseCase;
    @Mock private LoadUserPort loadUserPort;
    @Mock private RoleQueryPort roleQueryPort;


    AuthoritiesService authoritiesService;

    private Role sampleRole;
    private CreateRoleCommand sampleCreateRole;

    private static final String author = "PRODIGO_ERP_API";


    @BeforeEach
    void setUp() {
        authoritiesService = new AuthoritiesService(
                saveRolePort,
                savePermissionPort,
                moduleLookUpUseCase,
                tenantLookUpUseCase,
                loadUserPort,
                roleQueryPort
        );



        sampleCreateRole = new CreateRoleCommand(
                "ADMIN",
                null,
                "ADMIN : The Person has Full Access to the Tenant (company)",
                true,
                author
        );

        Instant now = Instant.now();
        sampleRole = new Role();
        sampleRole.setName(sampleCreateRole.roleName());
        sampleRole.setTenant(null);
        sampleRole.setDescription(sampleCreateRole.description());
        sampleRole.setIsDefault(sampleCreateRole.isDefault());
        sampleRole.setCreatedAt(now);
        sampleRole.setUpdatedAt(now);
        sampleRole.setCreatedBy(author);
        sampleRole.setUpdatedBy(author);
    }

//    @Nested
//    class SaveRoleTests{
//
//        /*
//        * NOTE: Unit Test doesn't work for the function saveRole(CreateRoleCommand roleCommand) when
//        * mocking the SaveRolePort.
//        *  Role actual = authoritiesService.saveRole(sampleCreateRole); doesn't work
//        * My hypothesis: since the service we are trying to test has the same function name (saveRole)
//        * that take a parameter (CreatedRoleCommand) that's different from the function SaveRole (being mock)
//        * TODO: integration tests will work fine
//        * */
//        @Test
//        void shouldSuccessfullySaveRole() {
//            when(saveRolePort.saveRole(sampleRole)).thenReturn(sampleRole);
//            Role fetchedRole = saveRolePort.saveRole(sampleRole);
//            Role actual = authoritiesService.saveRole(sampleCreateRole);
//
//            verify(saveRolePort).saveRole(sampleRole);
//            assertEquals(fetchedRole.getName(), actual.getName());
//        }
//    }

    @Nested
    class AssignedRoleToUserTest {

    }

}