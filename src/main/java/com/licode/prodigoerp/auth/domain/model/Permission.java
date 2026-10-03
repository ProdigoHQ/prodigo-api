package com.licode.prodigoerp.auth.domain.model;

import com.licode.prodigoerp.module.domain.model.Module;

import lombok.Data;


import java.time.Instant;
import java.util.UUID;

@Data
public class Permission {

    private UUID id;
    private String code;
    private String description;
    private String action;
    private String resource;
    private Module module;
    private boolean tenantAssignable;
    private Instant createdAt;
    private Instant updatedAt;
    private String createdBy;
    private String updatedBy;
}
