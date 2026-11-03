package com.licode.prodigoerp.tenant.application.service;

import com.licode.prodigoerp.auth.application.port.output.PermissionPersistencePort;
import com.licode.prodigoerp.auth.domain.model.Permission;
import com.licode.prodigoerp.common.exception.NotFoundException;
import com.licode.prodigoerp.module.adapter.output.persistence.module.JpaModuleRepository;
import com.licode.prodigoerp.module.adapter.output.persistence.module.ModuleJpaEntity;
import com.licode.prodigoerp.module.adapter.output.persistence.subscription.JpaModuleSubscriptionRepository;
import com.licode.prodigoerp.module.adapter.output.persistence.subscription.ModuleSubscriptionJpaEntity;
import com.licode.prodigoerp.module.application.port.input.query.GetTenantModuleDetailsQuery;
import com.licode.prodigoerp.module.application.port.input.result.ModuleDetailsResult;
import com.licode.prodigoerp.module.domain.model.Module;
import com.licode.prodigoerp.module.domain.model.SubscriptionStatus;
import com.licode.prodigoerp.tenant.adapter.output.persistence.Tenant.JpaTenantRepository;
import com.licode.prodigoerp.tenant.adapter.output.persistence.Tenant.TenantJpaEntity;
import com.licode.prodigoerp.tenant.application.port.output.TenantModuleQueryPort;
import com.licode.prodigoerp.tenant.domain.model.TenantStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
@Transactional
class TenantModuleServiceIntegrationTest {

    @Mock private PermissionPersistencePort permissionPort;

    @Autowired
    private TenantModuleQueryPort tenantModuleQueryPort;

    @Autowired
    private JpaTenantRepository jpaTenantRepository;

    @Autowired
    private JpaModuleRepository jpaModuleRepository;

    @Autowired
    private JpaModuleSubscriptionRepository jpaModuleSubscriptionRepository;

    private TenantModuleService tenantModuleService;

    private UUID tenantId;
    private Module crmModule;

    private final List<Permission> associatedFetchedPermissions = new ArrayList<>();

    @BeforeEach
    void setUp() {
        tenantModuleService = new TenantModuleService(
                tenantModuleQueryPort,
                permissionPort
        );

        Instant now = Instant.now();

        TenantJpaEntity tenant = new TenantJpaEntity();
        tenant.setName("Module Test Tenant");
        tenant.setSlug("module-test-" + UUID.randomUUID());
        tenant.setCountry("CM");
        tenant.setStatus(TenantStatus.ACTIVE);
        tenant.setCreatedAt(now);
        tenant.setUpdatedAt(now);
        tenant.setCreatedBy("TEST");
        tenant.setUpdatedBy("TEST");
        tenant = jpaTenantRepository.save(tenant);
        tenantId = tenant.getId();

        ModuleJpaEntity module = new ModuleJpaEntity();
        module.setName("Customer Relationship Management");
        module.setModuleKey("CRM_" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        module.setDescription("Test module");
        module.setPrice(BigDecimal.ZERO);
        module.setCurrency("XAF");
        module.setIsActive(true);
        module.setCreatedAt(now);
        module.setUpdatedAt(now);
        module.setCreatedBy("TEST");
        module.setUpdatedBy("TEST");
        module = jpaModuleRepository.save(module);

        ModuleSubscriptionJpaEntity subscription = new ModuleSubscriptionJpaEntity();
        subscription.setTenantJpaEntity(tenant);
        subscription.setModuleJpaEntity(module);
        subscription.setStatus(SubscriptionStatus.ACTIVE);
        subscription.setIsFree(true);
        subscription.setPrice(BigDecimal.ZERO);
        subscription.setCurrency("XAF");
        subscription.setActivatedAt(now);
        subscription.setCreatedAt(now);
        subscription.setUpdatedAt(now);
        subscription.setCreatedBy("TEST");
        subscription.setUpdatedBy("TEST");
        jpaModuleSubscriptionRepository.save(subscription);

        crmModule = new Module();
        crmModule.setId(module.getId());
        crmModule.setModuleKey(module.getModuleKey());
        crmModule.setName(module.getName());

        // add permissions
        for (int i = 0; i < 5 ; i++) {
            associatedFetchedPermissions.add(new Permission());
        }
    }

    @Test
    void findModuleByModuleKeyAndTenantId() {
        Optional<Module> fetchedModule = tenantModuleQueryPort.findModuleByModuleKeyAndTenantId(crmModule.getModuleKey(), tenantId);
        when(permissionPort.findPermissionsByModuleKey(crmModule.getModuleKey())).thenReturn(associatedFetchedPermissions);

        ModuleDetailsResult actual = tenantModuleService.findModuleWithPermissionsByKeyAndTenantId(
                new GetTenantModuleDetailsQuery(crmModule.getModuleKey(), tenantId));

        assertNotNull(actual);
        assertEquals(crmModule.getName(), actual.name());
        assertNotNull(fetchedModule);
        assertEquals(crmModule.getModuleKey(), fetchedModule.get().getModuleKey());
        verify(permissionPort).findPermissionsByModuleKey(crmModule.getModuleKey());
    }

    @Test
    void findModuleByModuleKeyAndTenantIdNotFound() {
        Optional<Module> fetchedModule = tenantModuleQueryPort.findModuleByModuleKeyAndTenantId("CRMS", tenantId);

        NotFoundException ex = assertThrows(NotFoundException.class, () -> {
            tenantModuleService.findModuleWithPermissionsByKeyAndTenantId(
                    new GetTenantModuleDetailsQuery("CRMS", tenantId));
        });

        assertEquals("No Module Found with this module key: 'CRMS' among  your subscription", ex.getMessage());
        assertEquals(Optional.empty(), fetchedModule);
    }


}
