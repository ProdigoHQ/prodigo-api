package com.licode.prodigoerp.tenant.application.service;

import com.licode.prodigoerp.auth.application.port.output.RoleQueryPort;
import com.licode.prodigoerp.auth.domain.model.Permission;
import com.licode.prodigoerp.common.exception.NotFoundException;
import com.licode.prodigoerp.module.application.port.input.command.ModuleSummaryCommand;
import com.licode.prodigoerp.module.domain.model.Module;
import com.licode.prodigoerp.tenant.application.port.output.TenantModuleQueryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
class TenantModuleServiceIntegrationTest {

    @Mock private RoleQueryPort roleQueryPort;

    @Autowired
    private TenantModuleQueryPort tenantModuleQueryPort;
    private TenantModuleService tenantModuleService;

    // 025a6330-9b7b-46b3-b493-b68acc3e44bd
    private final static UUID tenantId = UUID.fromString("025a6330-9b7b-46b3-b493-b68acc3e44bd");
    private final static UUID crmModuleId = UUID.fromString("28cdf5ea-9111-4f53-bffe-6ce76bab9c3a");
    private final List<Permission> associatedFetchedPermissions = new ArrayList<>();

    private final Module crmModule = new Module();


    @BeforeEach
    void setUp() {
        tenantModuleService = new TenantModuleService(
                tenantModuleQueryPort,
                roleQueryPort
        );

        crmModule.setId(crmModuleId);
        crmModule.setModuleKey("CRM");
        crmModule.setName("Customer Relationship Management");

        // add permissions
        for (int i = 0; i < 5 ; i++) {
            associatedFetchedPermissions.add(new Permission());
        }
    }

    @Test
    void findModuleByModuleKeyAndTenantId() {
        Optional<Module> fetchedModule = tenantModuleQueryPort.findModuleByModuleKeyAndTenantId(crmModule.getModuleKey(), tenantId);
        when(roleQueryPort.findPermissionsByModuleKey(crmModule.getModuleKey())).thenReturn(associatedFetchedPermissions);

        ModuleSummaryCommand actual = tenantModuleService.findModuleWithPermissionsByKeyAndTenantId(crmModule.getModuleKey(), tenantId);

        assertNotNull(actual);
        assertEquals(crmModule.getName(), actual.name());
        assertNotNull(fetchedModule);
        assertEquals(crmModule.getModuleKey(), fetchedModule.get().getModuleKey());
        verify(roleQueryPort).findPermissionsByModuleKey(crmModule.getModuleKey());
    }

    @Test
    void findModuleByModuleKeyAndTenantIdNotFound() {
        Optional<Module> fetchedModule = tenantModuleQueryPort.findModuleByModuleKeyAndTenantId("CRMS", tenantId);

        NotFoundException ex = assertThrows(NotFoundException.class, () -> {
            tenantModuleService.findModuleWithPermissionsByKeyAndTenantId("CRMS", tenantId);
        });

        assertEquals("No Module Found with this module key: 'CRMS' among  your subscription", ex.getMessage());
        assertEquals(Optional.empty(), fetchedModule);
    }


}