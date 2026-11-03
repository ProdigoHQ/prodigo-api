-- Normalize legacy case/whitespace variants to the enum names before adding
-- constraints. Unknown values are left untouched so the migration fails loudly
-- instead of silently discarding data.
UPDATE "tenants"
SET "status" = UPPER(TRIM("status"))
WHERE UPPER(TRIM("status")) IN ('PENDING', 'ACTIVE', 'SUSPENDED', 'DEACTIVATED')
  AND "status" <> UPPER(TRIM("status"));

UPDATE "users"
SET "status" = UPPER(TRIM("status"))
WHERE UPPER(TRIM("status")) IN ('PENDING', 'ACTIVE', 'SUSPENDED', 'DEACTIVATED', 'LOCKED')
  AND "status" <> UPPER(TRIM("status"));

UPDATE "module_subscriptions"
SET "status" = UPPER(TRIM("status"))
WHERE UPPER(TRIM("status")) IN ('PENDING', 'TRIALING', 'ACTIVE', 'PAST_DUE', 'SUSPENDED', 'CANCELLED', 'EXPIRED')
  AND "status" <> UPPER(TRIM("status"));

ALTER TABLE "tenants"
    ADD CONSTRAINT chk_tenants_status
        CHECK ("status" IN ('PENDING', 'ACTIVE', 'SUSPENDED', 'DEACTIVATED'));

ALTER TABLE "users"
    ADD CONSTRAINT chk_users_status
        CHECK ("status" IN ('PENDING', 'ACTIVE', 'SUSPENDED', 'DEACTIVATED', 'LOCKED'));

ALTER TABLE "module_subscriptions"
    ADD CONSTRAINT chk_module_subscriptions_status
        CHECK ("status" IN ('PENDING', 'TRIALING', 'ACTIVE', 'PAST_DUE', 'SUSPENDED', 'CANCELLED', 'EXPIRED'));
