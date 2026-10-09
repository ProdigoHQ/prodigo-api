package com.licode.prodigoerp.auth.application.service;

import com.licode.prodigoerp.auth.application.helpers.RolePermissionLinker;
import com.licode.prodigoerp.auth.application.port.output.LoadUserPort;
import com.licode.prodigoerp.auth.application.port.output.PermissionPersistencePort;
import com.licode.prodigoerp.auth.application.port.output.RolePersistencePort;
import com.licode.prodigoerp.auth.domain.model.Permission;
import com.licode.prodigoerp.auth.domain.model.Role;
import com.licode.prodigoerp.module.application.port.input.ModuleLookUpUseCase;
import com.licode.prodigoerp.tenant.application.port.input.TenantLookUpUseCase;
import com.licode.prodigoerp.tenant.domain.model.Tenant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccessProvisioningServiceTest {

    @Mock private RolePersistencePort rolePort;
    @Mock private PermissionPersistencePort permissionPort;
    @Mock private ModuleLookUpUseCase moduleLookUp;
    @Mock private TenantLookUpUseCase tenantLoopUp;
    @Mock private LoadUserPort loadUserPort;
    @Mock private RolePermissionLinker linker;

    // service to be tested
    private AccessProvisioningService accessProvisioningService;

    private static final UUID TENANT_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");
    private static final UUID ADMIN_ROLE_ID = UUID.fromString("44444444-4444-4444-4444-444444444444");
    private static final UUID TENANT_ROLE_ID = UUID.fromString("44444444-4444-4444-4444-444444444444");
    private static final UUID PERMISSION_ID = UUID.fromString("44444444-4444-4444-4444-444444444444");
    private static final String SUPER_ADMIN_ROLE = "SUPER_ADMIN";
    private static final String PERMISSION_PREFIX = "ERP.";
    private static final String author = "PRODIGO_ERP_API";



    private Role adminRole;
    private Role tenantRole;
    private List<Permission> permissions;
    private Set<String> codes;
    private Tenant tenant;
    List<String> moduleKeys;

    @BeforeEach
    void setUp(){
        accessProvisioningService = new AccessProvisioningService(
                rolePort,
                permissionPort,
                moduleLookUp,
                tenantLoopUp,
                loadUserPort,
                linker
        );

        tenant = new Tenant();
        tenant.setId(TENANT_ID);
        tenantRole = new Role();
        tenantRole.setId(TENANT_ROLE_ID);

        adminRole = new Role();
        adminRole.setId(ADMIN_ROLE_ID);
        adminRole.setName("ADMIN");

        permissions = new ArrayList<>();
        permissions.add(new Permission());
        permissions.add(new Permission());

        codes = Set.of(
                "USERS.MODULE.CRUD",
                "ROLES.MODULE.CRUD"
//                "PERMISSIONS.MODULE.CRUD",
//                "ORDERS.MODULE.CRUD",
//                "PRODUCTS.MODULE.CRUD",
//                "INVENTORY.MODULE.CRUD",
//                "CUSTOMERS.MODULE.CRUD",
//                "BILLING.MODULE.CRUD",
//                "REPORTS.MODULE.CRUD",
//                "SETTINGS.MODULE.CRUD"
        );

        moduleKeys = List.of(
                "users",
                "roles"
//                "orders",
//                "inventory",
//                "billing",
//                "reports",
//                "settings"
        );

    }


    @Nested
    class EnsureSuperAdminRoleTests{

        @Test
        void shouldSuccessfullyReturnSuperAdminRoleId(){
            when(rolePort.findSystemRoleByName(SUPER_ADMIN_ROLE)).thenReturn(Optional.of(adminRole));
            when(permissionPort.findAllByCodeStartingWith(PERMISSION_PREFIX)).thenReturn(permissions);
            when(rolePort.rolePermissionExists(adminRole.getId(), null)).thenReturn(true);

            UUID actual = accessProvisioningService.ensureSuperAdminRole(author);

            verify(rolePort, times(permissions.size())).rolePermissionExists(adminRole.getId(), null);

            assertEquals(ADMIN_ROLE_ID, actual);
            assertNotNull(permissionPort.findAllByCodeStartingWith(PERMISSION_PREFIX));
        }

        @Test
        void shouldReturnIllegalStateExceptionWhenPermissionsListIsEmpty(){
            when(rolePort.findSystemRoleByName(SUPER_ADMIN_ROLE)).thenReturn(Optional.of(adminRole));
            when(permissionPort.findAllByCodeStartingWith(PERMISSION_PREFIX)).thenReturn(List.of());

            IllegalStateException ex = assertThrows(IllegalStateException.class,
                    () -> accessProvisioningService.ensureSuperAdminRole(author));

            assertEquals("No ERP.* permissions found. Please contact SUPER ADMIN.",  ex.getMessage());
        }
    }

    @Nested
    class CreateTenantAdminRoleTests {

        @Test
        void shouldSuccessfullyReturnTenantAdminRoleId(){
            when(permissionPort.findAllByCodeIn(codes)).thenReturn(permissions);
            when(tenantLoopUp.findTenantById(TENANT_ID)).thenReturn(tenant);
            when(rolePort.save(any(Role.class))).thenReturn(tenantRole);

            UUID actual = accessProvisioningService.createTenantAdminRole(tenant.getId(), moduleKeys, author);

            assertEquals(permissionPort.findAllByCodeIn(codes).size(), codes.size());
            assertEquals(TENANT_ROLE_ID, actual);
            assertNotNull(permissionPort.findAllByCodeIn(codes));
        }
    }

}