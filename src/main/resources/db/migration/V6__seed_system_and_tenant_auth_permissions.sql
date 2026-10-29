INSERT INTO permissions (
    id,
    code,
    description,
    action,
    resource,
    module_id,
    created_at,
    updated_at,
    created_by,
    updated_by,
    tenant_assignable
)
SELECT
    gen_random_uuid(),
    p.code,
    p.description,
    p.action,
    p.resource,
    NULL,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP,
    'SYSTEM',
    'SYSTEM',
    p.tenant_assignable
FROM (
         VALUES
             -- Platform authentication security
             ('ERP.AUTH.CHANGE_PASSWORD', 'Change a platform account password', 'CHANGE_PASSWORD', 'AUTH', FALSE),
             ('ERP.AUTH.REVOKE_ALL_TOKENS', 'Revoke all authentication tokens for an account', 'REVOKE_ALL_TOKENS', 'AUTH', FALSE),
             ('ERP.AUTH.MANAGE_PROVIDERS', 'Configure platform authentication providers', 'MANAGE_PROVIDERS', 'AUTH', FALSE),
             ('ERP.AUTH.MANAGE_OAUTH', 'Configure platform OAuth integrations', 'MANAGE_OAUTH', 'AUTH', FALSE),

             -- Platform session management
             ('ERP.SESSION.READ', 'View a specific platform session', 'READ', 'SESSION', FALSE),
             ('ERP.SESSION.REVOKE', 'Revoke a specific platform session', 'REVOKE', 'SESSION', FALSE),
             ('ERP.SESSION.REVOKE_USER', 'Revoke all sessions belonging to a platform user', 'REVOKE_USER', 'SESSION', FALSE),

             -- Platform invitations
             ('ERP.INVITATION.CREATE', 'Create invitations for platform users', 'CREATE', 'INVITATION', FALSE),
             ('ERP.INVITATION.READ', 'View platform user invitations', 'READ', 'INVITATION', FALSE),
             ('ERP.INVITATION.REVOKE', 'Revoke a platform user invitation', 'REVOKE', 'INVITATION', FALSE),
             ('ERP.INVITATION.RESEND', 'Resend a platform user invitation', 'RESEND', 'INVITATION', FALSE),

             -- Platform API credentials
             ('ERP.API_KEY.CREATE', 'Create platform API keys', 'CREATE', 'API_KEY', FALSE),
             ('ERP.API_KEY.READ', 'View platform API key metadata', 'READ', 'API_KEY', FALSE),
             ('ERP.API_KEY.REVOKE', 'Revoke platform API keys', 'REVOKE', 'API_KEY', FALSE),
             ('ERP.API_KEY.ROTATE', 'Rotate platform API keys', 'ROTATE', 'API_KEY', FALSE),

             -- Platform security and compliance
             ('ERP.SECURITY.MANAGE_MFA_POLICY', 'Manage platform MFA enforcement policies', 'MANAGE_MFA_POLICY', 'SECURITY', FALSE),
             ('ERP.SECURITY.MANAGE_PASSWORD_POLICY', 'Manage platform password policies', 'MANAGE_PASSWORD_POLICY', 'SECURITY', FALSE),
             ('ERP.SECURITY.MANAGE_IP_ALLOWLIST', 'Manage platform IP allowlists', 'MANAGE_IP_ALLOWLIST', 'SECURITY', FALSE),
             ('ERP.SECURITY.READ_RISK_EVENTS', 'View platform authentication risk events', 'READ_RISK_EVENTS', 'SECURITY', FALSE),
             ('ERP.AUDIT.READ_AUTH_EVENTS', 'View platform authentication audit events', 'READ_AUTH_EVENTS', 'AUDIT', FALSE),

             -- Tenant authentication security
             ('TENANT.AUTH.REVOKE_ALL_TOKENS', 'Revoke all authentication tokens for a company user', 'REVOKE_ALL_TOKENS', 'AUTH', TRUE),
             ('TENANT.AUTH.MANAGE_OAUTH', 'Manage company OAuth connections', 'MANAGE_OAUTH', 'AUTH', TRUE),

             -- Tenant session management
             ('TENANT.SESSION.READ_USER', 'View sessions belonging to a company user', 'READ_USER', 'SESSION', TRUE),
             ('TENANT.SESSION.REVOKE_USER', 'Revoke sessions belonging to a company user', 'REVOKE_USER', 'SESSION', TRUE),

             -- Tenant invitations
             ('TENANT.INVITATION.CREATE', 'Invite users to join the company', 'CREATE', 'INVITATION', TRUE),
             ('TENANT.INVITATION.READ', 'View company user invitations', 'READ', 'INVITATION', TRUE),
             ('TENANT.INVITATION.REVOKE', 'Revoke a company user invitation', 'REVOKE', 'INVITATION', TRUE),
             ('TENANT.INVITATION.RESEND', 'Resend a company user invitation', 'RESEND', 'INVITATION', TRUE),

             -- Tenant API credentials
             ('TENANT.API_KEY.CREATE', 'Create company API keys', 'CREATE', 'API_KEY', TRUE),
             ('TENANT.API_KEY.READ', 'View company API key metadata', 'READ', 'API_KEY', TRUE),
             ('TENANT.API_KEY.REVOKE', 'Revoke company API keys', 'REVOKE', 'API_KEY', TRUE),
             ('TENANT.API_KEY.ROTATE', 'Rotate company API keys', 'ROTATE', 'API_KEY', TRUE),

             -- Tenant security and compliance
             ('TENANT.SECURITY.MANAGE_MFA_POLICY', 'Manage company MFA enforcement policies', 'MANAGE_MFA_POLICY', 'SECURITY', TRUE),
             ('TENANT.SECURITY.MANAGE_PASSWORD_POLICY', 'Manage company password policies', 'MANAGE_PASSWORD_POLICY', 'SECURITY', TRUE),
             ('TENANT.SECURITY.MANAGE_IP_ALLOWLIST', 'Manage company IP allowlists', 'MANAGE_IP_ALLOWLIST', 'SECURITY', TRUE),
             ('TENANT.SECURITY.READ_RISK_EVENTS', 'View company authentication risk events', 'READ_RISK_EVENTS', 'SECURITY', TRUE),
             ('TENANT.AUDIT.READ_AUTH_EVENTS', 'View company authentication audit events', 'READ_AUTH_EVENTS', 'AUDIT', TRUE)
     ) AS p(code, description, action, resource, tenant_assignable)
ON CONFLICT (code) DO NOTHING;

INSERT INTO permissions (
    id, code, description, action, resource, module_id,
    created_at, updated_at, created_by, updated_by, tenant_assignable
)
SELECT
    gen_random_uuid(),
    p.code,
    p.description,
    p.action,
    p.resource,
    NULL,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP,
    'SYSTEM',
    'SYSTEM',
    TRUE
FROM (
         VALUES
             -- Tenant authentication and account lifecycle
             ('TENANT.AUTH.LOGIN', 'Authenticate to a company workspace', 'LOGIN', 'AUTH'),
             ('TENANT.AUTH.LOGOUT', 'Log out of a company workspace', 'LOGOUT', 'AUTH'),
             ('TENANT.AUTH.REGISTER', 'Register a user for a company workspace', 'REGISTER', 'AUTH'),
             ('TENANT.AUTH.VERIFY_EMAIL', 'Verify a company user email address', 'VERIFY_EMAIL', 'AUTH'),
             ('TENANT.AUTH.RESEND_VERIFICATION', 'Resend company user email verification instructions', 'RESEND_VERIFICATION', 'AUTH'),
             ('TENANT.AUTH.FORGOT_PASSWORD', 'Request a company user password reset', 'FORGOT_PASSWORD', 'AUTH'),
             ('TENANT.AUTH.RESET_PASSWORD', 'Reset a company user password', 'RESET_PASSWORD', 'AUTH'),
             ('TENANT.AUTH.CHANGE_PASSWORD', 'Change the current company user password', 'CHANGE_PASSWORD', 'AUTH'),
             ('TENANT.AUTH.REFRESH_TOKEN', 'Refresh company workspace authentication tokens', 'REFRESH_TOKEN', 'AUTH'),
             ('TENANT.AUTH.REVOKE_TOKEN', 'Revoke a company workspace authentication token', 'REVOKE_TOKEN', 'AUTH'),
             ('TENANT.AUTH.VERIFY_MFA', 'Verify a company user multi-factor authentication challenge', 'VERIFY_MFA', 'AUTH'),
             ('TENANT.AUTH.ENABLE_MFA', 'Enable multi-factor authentication for a company user', 'ENABLE_MFA', 'AUTH'),
             ('TENANT.AUTH.DISABLE_MFA', 'Disable multi-factor authentication for a company user', 'DISABLE_MFA', 'AUTH'),
             ('TENANT.AUTH.SSO', 'Authenticate to a company workspace through single sign-on', 'SSO', 'AUTH'),

             -- Current user's profile
             ('TENANT.PROFILE.READ_OWN', 'View the current company user profile', 'READ_OWN', 'PROFILE'),
             ('TENANT.PROFILE.UPDATE_OWN', 'Update the current company user profile', 'UPDATE_OWN', 'PROFILE'),
             ('TENANT.PROFILE.DELETE_OWN', 'Request deletion of the current company user account', 'DELETE_OWN', 'PROFILE'),
             ('TENANT.PROFILE.UPLOAD_AVATAR', 'Upload or update the current company user avatar', 'UPLOAD_AVATAR', 'PROFILE'),
             ('TENANT.PROFILE.READ_SECURITY', 'View personal security settings in the company workspace', 'READ_SECURITY', 'PROFILE'),
             ('TENANT.PROFILE.UPDATE_SECURITY', 'Update personal security settings in the company workspace', 'UPDATE_SECURITY', 'PROFILE'),

             -- Tenant users
             ('TENANT.USER.CREATE', 'Create users within the company', 'CREATE', 'USER'),
             ('TENANT.USER.READ', 'View users within the company', 'READ', 'USER'),
             ('TENANT.USER.UPDATE', 'Update users within the company', 'UPDATE', 'USER'),
             ('TENANT.USER.DELETE', 'Remove users from the company', 'DELETE', 'USER'),
             ('TENANT.USER.ACTIVATE', 'Activate a company user', 'ACTIVATE', 'USER'),
             ('TENANT.USER.DEACTIVATE', 'Deactivate a company user', 'DEACTIVATE', 'USER'),
             ('TENANT.USER.RESET_PASSWORD', 'Reset a company user password', 'RESET_PASSWORD', 'USER'),
             ('TENANT.USER.MANAGE_ROLES', 'Assign or remove roles from company users', 'MANAGE_ROLES', 'USER'),
             ('TENANT.USER.READ_SECURITY', 'View a company user security profile', 'READ_SECURITY', 'USER'),

             -- Tenant roles
             ('TENANT.ROLE.CREATE', 'Create company roles', 'CREATE', 'ROLE'),
             ('TENANT.ROLE.READ', 'View company roles and permissions', 'READ', 'ROLE'),
             ('TENANT.ROLE.UPDATE', 'Update company roles', 'UPDATE', 'ROLE'),
             ('TENANT.ROLE.DELETE', 'Delete company roles', 'DELETE', 'ROLE'),
             ('TENANT.ROLE.ASSIGN', 'Assign roles to company users', 'ASSIGN', 'ROLE'),
             ('TENANT.ROLE.REVOKE', 'Remove roles from company users', 'REVOKE', 'ROLE'),
             ('TENANT.ROLE.READ_ASSIGNMENTS', 'View company user role assignments', 'READ_ASSIGNMENTS', 'ROLE'),

             -- Tenant permission assignments and policies
             ('TENANT.PERMISSION.READ', 'View permissions available to the company', 'READ', 'PERMISSION'),
             ('TENANT.PERMISSION.ASSIGN', 'Assign permissions to company roles', 'ASSIGN', 'PERMISSION'),
             ('TENANT.PERMISSION.REVOKE', 'Remove permissions from company roles', 'REVOKE', 'PERMISSION'),
             ('TENANT.PERMISSION.READ_ASSIGNMENTS', 'View company role permission assignments', 'READ_ASSIGNMENTS', 'PERMISSION'),
             ('TENANT.PERMISSION.MANAGE_ASSIGNMENTS', 'Manage company role permission assignments', 'MANAGE_ASSIGNMENTS', 'PERMISSION'),
             ('TENANT.POLICY.CREATE', 'Create company authorization policies', 'CREATE', 'POLICY'),
             ('TENANT.POLICY.READ', 'View company authorization policies', 'READ', 'POLICY'),
             ('TENANT.POLICY.UPDATE', 'Update company authorization policies', 'UPDATE', 'POLICY'),
             ('TENANT.POLICY.DELETE', 'Delete company authorization policies', 'DELETE', 'POLICY'),

             -- Tenant sessions and audit
             ('TENANT.SESSION.READ_OWN', 'View the current company user sessions', 'READ_OWN', 'SESSION'),
             ('TENANT.SESSION.REVOKE_OWN', 'Revoke one of the current company user sessions', 'REVOKE_OWN', 'SESSION'),
             ('TENANT.SESSION.REVOKE_ALL_OWN', 'Revoke all sessions belonging to the current company user', 'REVOKE_ALL_OWN', 'SESSION'),
             ('TENANT.SESSION.READ', 'View user sessions within the company', 'READ', 'SESSION'),
             ('TENANT.SESSION.REVOKE', 'Revoke user sessions within the company', 'REVOKE', 'SESSION'),
             ('TENANT.AUDIT.READ', 'View company security and audit events', 'READ', 'AUDIT'),
             ('TENANT.AUDIT.EXPORT', 'Export company audit records', 'EXPORT', 'AUDIT'),
             ('TENANT.SECURITY.READ', 'View company security status', 'READ', 'SECURITY'),
             ('TENANT.SECURITY.CONFIGURE', 'Configure company security policies', 'CONFIGURE', 'SECURITY'),
             ('TENANT.SECURITY.READ_LOGIN_HISTORY', 'View company authentication history', 'READ_LOGIN_HISTORY', 'SECURITY'),
             ('TENANT.SECURITY.MANAGE_LOCKOUTS', 'Manage company account lockout policies', 'MANAGE_LOCKOUTS', 'SECURITY'),

             -- Company profile and settings
             ('TENANT.COMPANY.READ', 'View company profile and details', 'READ', 'COMPANY'),
             ('TENANT.COMPANY.UPDATE', 'Update company profile and details', 'UPDATE', 'COMPANY'),
             ('TENANT.COMPANY.READ_SETTINGS', 'View company settings', 'READ_SETTINGS', 'COMPANY'),
             ('TENANT.COMPANY.UPDATE_SETTINGS', 'Update company settings', 'UPDATE_SETTINGS', 'COMPANY')
     ) AS p(code, description, action, resource)
ON CONFLICT (code) DO NOTHING;