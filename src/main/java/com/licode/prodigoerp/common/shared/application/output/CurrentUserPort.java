package com.licode.prodigoerp.common.shared.application.output;

import java.util.UUID;

public interface CurrentUserPort {
    String username();
    UUID requireTenantId();                 // throws if the caller has no tenant
    boolean hasPermission(String code);     // checks PERM_<code>
    String usernameOrSystem();
}
