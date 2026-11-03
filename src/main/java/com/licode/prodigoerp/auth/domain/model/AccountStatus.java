package com.licode.prodigoerp.auth.domain.model;

/**
 * Lifecycle and accessibility of a user account, shared by platform
 * administrators and tenant users.
 */
public enum AccountStatus {
    PENDING,
    ACTIVE,
    SUSPENDED,
    DEACTIVATED,
    LOCKED
}
