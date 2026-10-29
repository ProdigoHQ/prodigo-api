INSERT INTO roles
    (id, tenant_id, name, description, is_default,
     created_at, updated_at, created_by, updated_by)
VALUES
    (gen_random_uuid(), NULL, 'SUPER_ADMIN', 'Platform super administrator',
     FALSE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM'),
    (gen_random_uuid(), NULL, 'PLATFORM_ADMIN', 'Platform administrator',
     FALSE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM'),
    (gen_random_uuid(), NULL, 'PLATFORM_AUDITOR', 'Platform read-only auditor',
     FALSE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM')
ON CONFLICT (name) WHERE tenant_id IS NULL DO NOTHING;
