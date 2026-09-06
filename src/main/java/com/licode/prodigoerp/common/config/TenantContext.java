package com.licode.prodigoerp.common.config;

import java.util.UUID;

public class TenantContext {

    public static ThreadLocal<UUID> currentTenant =  new ThreadLocal<>();

    public static void setCurrentTenant(UUID tenantId) {
        if(tenantId != null) {
            currentTenant.set(tenantId);
        }
    }

    public static UUID getCurrentTenant() {
        return currentTenant.get();
    }

    public static void clearCurrentTenant() {
        currentTenant.remove();
    }
}
