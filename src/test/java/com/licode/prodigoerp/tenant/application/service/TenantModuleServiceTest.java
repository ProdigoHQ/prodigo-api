package com.licode.prodigoerp.tenant.application.service;

import com.licode.prodigoerp.auth.application.port.output.RoleQueryPort;
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
    private static final List<Module> allModules =  new ArrayList<>();
//    private static final List<ShowPublicModuleCommand> allShowPublicModuleCommands =  new ArrayList<>();

    @BeforeEach
    void setUp(){

        tenantModuleService = new TenantModuleService(
                tenantModuleQueryPort, roleQueryPort
        );
    }

    @Nested
    @DisplayName("Test to find all active modules by TenantId")
    class FindAllActiveModulesByTenantId {

        @Test
        void testFindAllActiveModulesByTenantId() {

            // Given
            when(tenantModuleQueryPort.findAllActiveModulesByTenantId(tenantId)).thenReturn(allModules);

            // when
            List<ShowPublicModuleCommand> actual = tenantModuleService.findAllActiveModulesByTenantId(tenantId);

            // then
            verify(tenantModuleQueryPort).findAllActiveModulesByTenantId(tenantId);
        }
    }


}