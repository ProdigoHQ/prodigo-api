package com.licode.prodigoerp.common.config;

import java.util.UUID;

public class TenantContext {

    private static final ThreadLocal<UUID> CURRENT_TENANT =  new ThreadLocal<>();

    public static void setCurrentTenant(UUID tenantId) {
        if(tenantId != null) {
            CURRENT_TENANT.set(tenantId);
        }
    }

    public static UUID getCurrentTenant() {
        return CURRENT_TENANT.get();
    }

    public static void clearCurrentTenant() {
        CURRENT_TENANT.remove();
    }
}
