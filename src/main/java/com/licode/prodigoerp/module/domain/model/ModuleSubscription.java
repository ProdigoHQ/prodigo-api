package com.licode.prodigoerp.module.domain.model;

import com.licode.prodigoerp.tenant.domain.model.Tenant;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;


@Data
public class ModuleSubscription {

    private UUID id;
    private Tenant  tenant;
    private Module module;
    private SubscriptionStatus status;
    private Boolean isFree;
    private BigDecimal price;
    private String currency;
    private Instant activatedAt;
    private Instant expiresAt;
    private Instant createdAt;
    private Instant updatedAt;
    private String createdBy;
    private String updatedBy;
}
