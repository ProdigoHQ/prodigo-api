package com.licode.prodigoerp.tenant.adapter.output.persistence.Tenant;

import com.licode.prodigoerp.module.adapter.output.persistence.module.ModuleJpaEntity;
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

    boolean existsTenantJpaEntityByIdAndStatus(UUID id, String status);

    @Modifying
    @Transactional
    @Query("UPDATE TenantJpaEntity t SET t.status = :status WHERE t.slug = :slug")
    void changeStatus(@NotBlank String slug, @NotBlank @Param("status") String status);

    @Query("select m from ModuleSubscriptionJpaEntity ms INNER join ModuleJpaEntity m ON ms.moduleJpaEntity.id = m.id where ms.tenantJpaEntity.id = :tenantId and ms.status = 'ACTIVE'")
    List<ModuleJpaEntity> findModuleByTenantIdWhereStatusIsActive(UUID tenantId);

}
