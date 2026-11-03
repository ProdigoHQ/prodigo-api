package com.licode.prodigoerp.tenant.adapter.output.persistence.Tenant;

import com.licode.prodigoerp.module.adapter.output.persistence.module.ModuleJpaEntity;
import com.licode.prodigoerp.module.domain.model.SubscriptionStatus;
import com.licode.prodigoerp.tenant.domain.model.TenantStatus;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface JpaTenantRepository extends JpaRepository<TenantJpaEntity, UUID> {

    boolean existsBySlug(String slug);
    Optional<TenantJpaEntity> findBySlug(String slug);

    boolean existsTenantJpaEntityByIdAndStatus(UUID id, TenantStatus status);

    @Modifying
    @Transactional
    @Query("UPDATE TenantJpaEntity t SET t.status = :status WHERE t.slug = :slug")
    void changeStatus(@NotBlank String slug, @Param("status") TenantStatus status);

    @Query("""
            select m
            from ModuleSubscriptionJpaEntity ms
            inner join ModuleJpaEntity m on ms.moduleJpaEntity.id = m.id
            where ms.tenantJpaEntity.id = :tenantId
              and ms.status = :status
            """)
    List<ModuleJpaEntity> findModuleByTenantIdWhereStatusIsActive(
            @Param("tenantId") UUID tenantId,
            @Param("status") SubscriptionStatus status);

    @Query("SELECT m FROM ModuleJpaEntity m LEFT JOIN ModuleSubscriptionJpaEntity ms ON m.id = ms.moduleJpaEntity.id AND ms.tenantJpaEntity.id = :tenantId WHERE ms.id IS NULL AND m.isActive = true")
    List<ModuleJpaEntity> findModuleWhereTenantIdNotEquals(UUID tenantId);
}
