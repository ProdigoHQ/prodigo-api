package com.licode.prodigoerp.tenant.domain.model;


import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
public class Tenant {
    private UUID id;
    private String name;
    private String slug;
    private String country;
    private TenantStatus status;
    private Instant createdAt;
    private Instant updatedAt;
    private String createdBy;
    private String updatedBy;
}
