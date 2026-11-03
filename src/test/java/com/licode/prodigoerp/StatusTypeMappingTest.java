package com.licode.prodigoerp;

import com.licode.prodigoerp.auth.adapter.input.rest.dto.response.UserSummaryResponse;
import com.licode.prodigoerp.auth.adapter.output.persistence.user.UserJpaEntity;
import com.licode.prodigoerp.auth.adapter.output.persistence.user.UserJpaMapper;
import com.licode.prodigoerp.auth.domain.model.AccountStatus;
import com.licode.prodigoerp.auth.domain.model.User;
import com.licode.prodigoerp.module.adapter.output.persistence.subscription.ModuleSubscriptionJpaEntity;
import com.licode.prodigoerp.module.adapter.output.persistence.subscription.ModuleSubscriptionJpaMapper;
import com.licode.prodigoerp.module.domain.model.Module;
import com.licode.prodigoerp.module.domain.model.ModuleSubscription;
import com.licode.prodigoerp.module.domain.model.SubscriptionStatus;
import com.licode.prodigoerp.tenant.adapter.output.persistence.Tenant.TenantJpaEntity;
import com.licode.prodigoerp.tenant.adapter.output.persistence.Tenant.TenantJpaMapper;
import com.licode.prodigoerp.tenant.domain.model.Tenant;
import com.licode.prodigoerp.tenant.domain.model.TenantStatus;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotNull;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.json.JsonMapper;

import java.lang.reflect.Field;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StatusTypeMappingTest {

    private final JsonMapper jsonMapper = JsonMapper.builder().build();

    @Test
    void userJpaMapperMapsAccountStatusBothWays() {
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setStatus(AccountStatus.SUSPENDED);

        UserJpaEntity entity = UserJpaMapper.toJpaEntity(user);
        assertEquals(AccountStatus.SUSPENDED, entity.getStatus());

        User mappedBack = UserJpaMapper.toDomainModel(entity);
        assertEquals(AccountStatus.SUSPENDED, mappedBack.getStatus());
    }

    @Test
    void tenantJpaMapperMapsTenantStatusBothWays() {
        Tenant tenant = new Tenant();
        tenant.setId(UUID.randomUUID());
        tenant.setStatus(TenantStatus.DEACTIVATED);

        TenantJpaEntity entity = TenantJpaMapper.toJpaEntity(tenant);
        assertEquals(TenantStatus.DEACTIVATED, entity.getStatus());

        Tenant mappedBack = TenantJpaMapper.toDomainModel(entity);
        assertEquals(TenantStatus.DEACTIVATED, mappedBack.getStatus());
    }

    @Test
    void moduleSubscriptionJpaMapperMapsSubscriptionStatusBothWays() {
        Tenant tenant = new Tenant();
        tenant.setId(UUID.randomUUID());
        tenant.setStatus(TenantStatus.ACTIVE);

        Module module = new Module();
        module.setId(UUID.randomUUID());

        ModuleSubscription subscription = new ModuleSubscription();
        subscription.setId(UUID.randomUUID());
        subscription.setTenant(tenant);
        subscription.setModule(module);
        subscription.setStatus(SubscriptionStatus.PAST_DUE);

        ModuleSubscriptionJpaEntity entity = ModuleSubscriptionJpaMapper.toJpaEntity(subscription);
        assertEquals(SubscriptionStatus.PAST_DUE, entity.getStatus());

        ModuleSubscription mappedBack = ModuleSubscriptionJpaMapper.toDomainModel(entity);
        assertEquals(SubscriptionStatus.PAST_DUE, mappedBack.getStatus());
    }

    @Test
    void restDtoSerializesAndDeserializesAccountStatusUsingEnumNames() throws Exception {
        UserSummaryResponse response = new UserSummaryResponse(
                UUID.randomUUID(),
                "jdoe",
                "jdoe@example.com",
                "John",
                "Doe",
                AccountStatus.LOCKED,
                null,
                null
        );

        String json = jsonMapper.writeValueAsString(response);
        assertTrue(json.contains("\"status\":\"LOCKED\""));

        UserSummaryResponse parsed = jsonMapper.readValue(json, UserSummaryResponse.class);
        assertEquals(AccountStatus.LOCKED, parsed.status());
    }

    @Test
    void statusColumnsUseStringEnumPersistence() throws NoSuchFieldException {
        assertStringEnum(UserJpaEntity.class, "status");
        assertStringEnum(TenantJpaEntity.class, "status");
        assertStringEnum(ModuleSubscriptionJpaEntity.class, "status");
    }

    private void assertStringEnum(Class<?> entityType, String fieldName) throws NoSuchFieldException {
        Field field = entityType.getDeclaredField(fieldName);
        Enumerated enumerated = field.getAnnotation(Enumerated.class);

        assertNotNull(enumerated);
        assertEquals(EnumType.STRING, enumerated.value());
    }

    @Test
    void lastLoginRemainsNullableInJpaEntity() throws NoSuchFieldException {
        Field lastLogin = UserJpaEntity.class.getDeclaredField("lastLogin");

        assertTrue(lastLogin.getAnnotation(Column.class).nullable());
        assertNull(lastLogin.getAnnotation(NotNull.class));
    }
}
