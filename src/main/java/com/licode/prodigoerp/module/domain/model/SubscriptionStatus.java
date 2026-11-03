package com.licode.prodigoerp.module.domain.model;

/**
 * Lifecycle of a module subscription.
 */
public enum SubscriptionStatus {
    PENDING,
    TRIALING,
    ACTIVE,
    PAST_DUE,
    SUSPENDED,
    CANCELLED,
    EXPIRED
}
