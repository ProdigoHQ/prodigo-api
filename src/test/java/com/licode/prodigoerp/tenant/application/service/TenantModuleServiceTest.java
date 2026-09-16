package com.licode.prodigoerp.tenant.application.service;

import com.licode.prodigoerp.auth.application.port.output.RoleQueryPort;
import com.licode.prodigoerp.auth.domain.model.Permission;
import com.licode.prodigoerp.common.exception.NotFoundException;
import com.licode.prodigoerp.module.application.port.input.command.ModuleSummaryCommand;
import com.licode.prodigoerp.module.application.port.input.command.ShowPublicModuleCommand;
import com.licode.prodigoerp.module.domain.model.Module;
import com.licode.prodigoerp.tenant.application.port.output.TenantModuleQueryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TenantModuleServiceTest {

    @Mock private TenantModuleQueryPort tenantModuleQueryPort;
    @Mock private RoleQueryPort roleQueryPort;

    private TenantModuleService tenantModuleService; // what we want to test

    private static final UUID tenantId = UUID.fromString("11111111-1111-1111-1111-111111111111");
    private static final String moduleKey = "CRM";
    private static final UUID CRM_MODULE_ID = UUID.fromString("55555555-5555-5555-5555-555555555555");

    private final List<Module> allModules = new ArrayList<>();
    private final List<Module> tenantModules = new ArrayList<>();
    private final List<Module> remainingModules = new ArrayList<>();
    private Module crmModule;
    private final List<Permission> associatedFetchedPermissions = new ArrayList<>();
//    private static final List<ShowPublicModuleCommand> allShowPublicModuleCommands =  new ArrayList<>();

    @BeforeEach
    void setUp(){

        tenantModuleService = new TenantModuleService(
                tenantModuleQueryPort, roleQueryPort
        );

        // add 10 modules in the all modules
        for (int i = 0; i < 10 ; i++) {
            allModules.add(new Module());
        }

        // add 3 modules for the tenant
        for (int i = 0; i < 3; i++) {
            tenantModules.add(new Module());
        }

        // remaining modules
        for (int i = 0; i < 10 - 3; i++) {
            remainingModules.add(new Module());
        }

        crmModule = new Module();
        crmModule.setId(CRM_MODULE_ID);
        crmModule.setName("CRM");
        crmModule.setModuleKey(moduleKey);

        // add permissions
        for (int i = 0; i < 5 ; i++) {
            associatedFetchedPermissions.add(new Permission());
        }
    }

    @Nested
    @DisplayName("Testing findAllActiveModulesByTenantId function")
    class FindAllActiveModulesByTenantId {

        @Test
        @DisplayName("Happy path: Test to find all active modules by TenantId (3 modules)")
        void testFindAllActiveModulesByTenantId() {

            // Given
            when(tenantModuleQueryPort.findAllActiveModulesByTenantId(tenantId)).thenReturn(tenantModules);

            // when
            List<ShowPublicModuleCommand> actual = tenantModuleService.findAllActiveModulesByTenantId(tenantId);

            // then
            verify(tenantModuleQueryPort).findAllActiveModulesByTenantId(tenantId);
            assertEquals(tenantModules.size(), actual.size());
            assertNotEquals(9, actual.size());
        }
    }

    @Nested
    @DisplayName("Testing the findAllAvailableModulesToPay function")
    class FindAllActiveModulesNotSubByTenantId {

        @Test
        @DisplayName("Happy path Test: find all active modules not sub by the tenant (7 modules)")
        void findAllAvailableModulesToPay() {
            // Given
            when(tenantModuleQueryPort.findAllAvailableModulesToPay(tenantId)).thenReturn(remainingModules);

            List<ShowPublicModuleCommand> actual = tenantModuleService.findAllAvailableModulesToPay(tenantId);

            verify(tenantModuleQueryPort).findAllAvailableModulesToPay(tenantId);
            assertEquals(remainingModules.size(), actual.size());
            assertNotEquals(allModules.size(), actual.size());
        }

    }

    @Nested
    @DisplayName("Testing the findModuleWithPermissionsByKeyAndTenantId function")
    class FindModuleWithPermissionsByKeyAndTenantId {

        @Test
        @DisplayName("Happy path Test: find a module details and its permissions")
        void findModuleWithPermissionsByKeyAndTenantId() {
            when(tenantModuleQueryPort.findModuleByModuleKeyAndTenantId(moduleKey, tenantId)).thenReturn(Optional.of(crmModule));
            when(roleQueryPort.findPermissionsByModuleKey(moduleKey)).thenReturn(associatedFetchedPermissions);

            ModuleSummaryCommand  actual = tenantModuleService.findModuleWithPermissionsByKeyAndTenantId(moduleKey, tenantId);

            verify(tenantModuleQueryPort).findModuleByModuleKeyAndTenantId(moduleKey, tenantId);
            assertEquals(crmModule.getName(), actual.name());
            assertEquals(associatedFetchedPermissions.size(), actual.permissions().size());
        }

        @Test
        @DisplayName("Sad path: throw error if module not found")
        void findModuleWithPermissionsByKeyAndTenantIdNotFound() {

            when(tenantModuleQueryPort.findModuleByModuleKeyAndTenantId(moduleKey, tenantId)).thenReturn(Optional.empty());

            NotFoundException ex = assertThrows(NotFoundException.class,
                    () -> tenantModuleService.findModuleWithPermissionsByKeyAndTenantId(moduleKey, tenantId));

            assertEquals("No Module Found with this module key: '" + moduleKey + "' among  your subscription", ex.getMessage());
        }
    }

}