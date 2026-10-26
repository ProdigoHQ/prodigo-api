package com.licode.prodigoerp.module.adapter.output.persistence.subscription;

import com.licode.prodigoerp.module.adapter.output.persistence.module.ModuleJpaEntity;
import com.licode.prodigoerp.tenant.adapter.output.persistence.Tenant.TenantJpaEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "module_subscriptions")
public class ModuleSubscriptionJpaEntity {
    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(name = "id", nullable = false,  updatable = false)
    private UUID id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "tenant_id", nullable = false)
    private TenantJpaEntity tenantJpaEntity;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "module_id", nullable = false)
    private ModuleJpaEntity moduleJpaEntity;

    @Size(max = 30)
    @NotNull
    @ColumnDefault("'ACTIVE'")
    @Column(name = "status", nullable = false, length = 30)
    private String status;

    @NotNull
    @ColumnDefault("false")
    @Column(name = "is_free", nullable = false)
    private Boolean isFree;

    @NotNull
    @ColumnDefault("0")
    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Size(max = 3)
    @NotNull
    @ColumnDefault("'XAF'")
    @Column(name = "currency", nullable = false, length = 3)
    private String currency;

    @NotNull
    @Column(name = "activated_at", nullable = false)
    private Instant activatedAt;

    @Column(name = "expires_at")
    private Instant expiresAt;

    @NotNull
    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @NotNull
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Size(max = 100)
    @NotNull
    @Column(name = "created_by", nullable = false, length = 100)
    private String createdBy;

    @Size(max = 100)
    @NotNull
    @Column(name = "updated_by", nullable = false, length = 100)
    private String updatedBy;


}