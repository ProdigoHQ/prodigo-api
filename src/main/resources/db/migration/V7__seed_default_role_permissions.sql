-- =============================================================================
-- V7: permissions for the default system and tenant roles
--
-- Platform roles are created in V4.
-- Tenant roles (OWNER / ADMIN / MEMBER) are created at runtime by the
-- onboarding flow, so a trigger grants their TENANT.* permissions whenever
-- the role is inserted.
-- =============================================================================

-- -----------------------------------------------------------------------------
-- SUPER_ADMIN: every platform permission, including the ones seeded in V6.
-- (V5 already granted the ERP.* permissions that existed at that time.)
-- -----------------------------------------------------------------------------
INSERT INTO role_permissions (id, role_id, permission_id, granted_at, granted_by)
SELECT gen_random_uuid(), r.id, p.id, CURRENT_TIMESTAMP, 'SYSTEM'
FROM roles r
JOIN permissions p ON p.code LIKE 'ERP.%'
WHERE r.name = 'SUPER_ADMIN'
  AND r.tenant_id IS NULL
ON CONFLICT (role_id, permission_id) DO NOTHING;

-- -----------------------------------------------------------------------------
-- PLATFORM_ADMIN: every ERP.* permission except platform-destructive or
-- system-maintenance operations (reserved for SUPER_ADMIN).
-- -----------------------------------------------------------------------------
INSERT INTO role_permissions (id, role_id, permission_id, granted_at, granted_by)
SELECT gen_random_uuid(), r.id, p.id, CURRENT_TIMESTAMP, 'SYSTEM'
FROM roles r
JOIN permissions p ON p.code LIKE 'ERP.%'
WHERE r.name = 'PLATFORM_ADMIN'
  AND r.tenant_id IS NULL
  AND p.code NOT IN (
      'ERP.ADMIN.DELETE',
      'ERP.MODULE.DELETE',
      'ERP.TENANT.DELETE',
      'ERP.SUBSCRIPTION.DELETE',
      'ERP.SYSTEM.UPDATE',
      'ERP.SYSTEM.MAINTENANCE',
      'ERP.SYSTEM.CONFIGURE'
  )
ON CONFLICT (role_id, permission_id) DO NOTHING;

-- -----------------------------------------------------------------------------
-- PLATFORM_AUDITOR: read-only platform access.
-- -----------------------------------------------------------------------------
INSERT INTO role_permissions (id, role_id, permission_id, granted_at, granted_by)
SELECT gen_random_uuid(), r.id, p.id, CURRENT_TIMESTAMP, 'SYSTEM'
FROM roles r
JOIN permissions p ON p.code IN (
    'ERP.ADMIN.READ',
    'ERP.USER.READ',
    'ERP.ROLE.READ',
    'ERP.PERMISSION.READ',
    'ERP.MODULE.READ',
    'ERP.TENANT.READ',
    'ERP.TENANT.READ_USERS',
    'ERP.TENANT.READ_USAGE',
    'ERP.SUBSCRIPTION.READ',
    'ERP.ENTITLEMENT.READ',
    'ERP.BILLING.READ',
    'ERP.PAYMENT.READ',
    'ERP.INVOICE.READ',
    'ERP.SYSTEM.ACCESS',
    'ERP.SYSTEM.READ',
    'ERP.SYSTEM.MONITOR',
    'ERP.SYSTEM.LOGS',
    'ERP.SESSION.READ',
    'ERP.INVITATION.READ',
    'ERP.API_KEY.READ',
    'ERP.SECURITY.READ_RISK_EVENTS',
    'ERP.AUDIT.READ_AUTH_EVENTS'
)
WHERE r.name = 'PLATFORM_AUDITOR'
  AND r.tenant_id IS NULL
ON CONFLICT (role_id, permission_id) DO NOTHING;

-- -----------------------------------------------------------------------------
-- Tenant default roles.
--
-- Only TENANT.* permissions (module_id IS NULL) are handled here: the module
-- permissions (*.MODULE.CRUD) are still linked by AccessProvisioningService
-- for the modules the tenant actually subscribed to.
-- -----------------------------------------------------------------------------
CREATE OR REPLACE FUNCTION seed_tenant_default_role_permissions()
RETURNS TRIGGER
LANGUAGE plpgsql
AS $function$
BEGIN
    IF NEW."tenant_id" IS NULL THEN
        RETURN NEW;
    END IF;

    IF UPPER(NEW."name") = 'OWNER' THEN
        INSERT INTO role_permissions (id, role_id, permission_id, granted_at, granted_by)
        SELECT gen_random_uuid(), NEW."id", p."id", CURRENT_TIMESTAMP, 'SYSTEM'
        FROM permissions p
        WHERE p."tenant_assignable" = TRUE
          AND p."module_id" IS NULL
        ON CONFLICT (role_id, permission_id) DO NOTHING;

    ELSIF UPPER(NEW."name") = 'ADMIN' THEN
        INSERT INTO role_permissions (id, role_id, permission_id, granted_at, granted_by)
        SELECT gen_random_uuid(), NEW."id", p."id", CURRENT_TIMESTAMP, 'SYSTEM'
        FROM permissions p
        WHERE p."tenant_assignable" = TRUE
          AND p."module_id" IS NULL
          AND p."code" NOT IN (
              'TENANT.COMPANY.UPDATE',
              'TENANT.COMPANY.UPDATE_SETTINGS'
          )
        ON CONFLICT (role_id, permission_id) DO NOTHING;

    ELSIF UPPER(NEW."name") = 'MEMBER' THEN
        INSERT INTO role_permissions (id, role_id, permission_id, granted_at, granted_by)
        SELECT gen_random_uuid(), NEW."id", p."id", CURRENT_TIMESTAMP, 'SYSTEM'
        FROM permissions p
        WHERE p."tenant_assignable" = TRUE
          AND p."module_id" IS NULL
          AND p."code" IN (
              'TENANT.AUTH.LOGIN',
              'TENANT.AUTH.LOGOUT',
              'TENANT.AUTH.REFRESH_TOKEN',
              'TENANT.AUTH.REVOKE_TOKEN',
              'TENANT.AUTH.CHANGE_PASSWORD',
              'TENANT.AUTH.FORGOT_PASSWORD',
              'TENANT.AUTH.RESET_PASSWORD',
              'TENANT.AUTH.VERIFY_EMAIL',
              'TENANT.AUTH.RESEND_VERIFICATION',
              'TENANT.AUTH.VERIFY_MFA',
              'TENANT.AUTH.ENABLE_MFA',
              'TENANT.AUTH.DISABLE_MFA',
              'TENANT.AUTH.SSO',
              'TENANT.PROFILE.READ_OWN',
              'TENANT.PROFILE.UPDATE_OWN',
              'TENANT.PROFILE.DELETE_OWN',
              'TENANT.PROFILE.UPLOAD_AVATAR',
              'TENANT.PROFILE.READ_SECURITY',
              'TENANT.PROFILE.UPDATE_SECURITY',
              'TENANT.SESSION.READ_OWN',
              'TENANT.SESSION.REVOKE_OWN',
              'TENANT.SESSION.REVOKE_ALL_OWN',
              'TENANT.COMPANY.READ',
              'TENANT.ROLE.READ',
              'TENANT.PERMISSION.READ'
          )
        ON CONFLICT (role_id, permission_id) DO NOTHING;
    END IF;

    RETURN NEW;
END;
$function$;

DROP TRIGGER IF EXISTS trg_seed_tenant_default_role_permissions ON roles;

CREATE TRIGGER trg_seed_tenant_default_role_permissions
AFTER INSERT ON roles
FOR EACH ROW
EXECUTE FUNCTION seed_tenant_default_role_permissions();
