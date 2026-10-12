INSERT INTO role_permissions (id, role_id, permission_id, granted_at, granted_by)
SELECT gen_random_uuid(), r.id, p.id, CURRENT_TIMESTAMP, 'SYSTEM'
FROM roles r
JOIN permissions p ON p.code LIKE 'ERP.%'
WHERE r.name = 'SUPER_ADMIN' AND r.tenant_id IS NULL
ON CONFLICT (role_id, permission_id) DO NOTHING;
