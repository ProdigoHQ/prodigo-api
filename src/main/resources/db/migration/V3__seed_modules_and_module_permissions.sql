INSERT INTO modules
    (id, name, module_key, description, price, currency, is_active,
     created_at, updated_at, created_by, updated_by)
VALUES
    (gen_random_uuid(), 'Human Resources', 'HR', 'Human resources management', 0, 'XAF', TRUE,
     CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM'),
    (gen_random_uuid(), 'Inventory', 'INVENTORY', 'Inventory management', 0, 'XAF', TRUE,
     CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM')
ON CONFLICT DO NOTHING;

INSERT INTO permissions
    (id, code, description, action, resource, module_id,
     created_at, updated_at, created_by, updated_by, tenant_assignable)
SELECT gen_random_uuid(),
       m.module_key || '.' || p.resource || '.' || p.action,
       p.description,
       p.action,
       p.resource,
       m.id,
       CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM', TRUE
FROM modules m
JOIN (VALUES
    ('HR', 'EMPLOYEE', 'CREATE', 'Create employee records'),
    ('HR', 'EMPLOYEE', 'READ', 'View employee information'),
    ('HR', 'EMPLOYEE', 'UPDATE', 'Modify employee information'),
    ('HR', 'EMPLOYEE', 'DELETE', 'Delete employee records'),
    ('HR', 'DEPARTMENT', 'CREATE', 'Create departments'),
    ('HR', 'DEPARTMENT', 'READ', 'View departments'),
    ('HR', 'DEPARTMENT', 'UPDATE', 'Modify departments'),
    ('HR', 'DEPARTMENT', 'DELETE', 'Delete departments'),
    ('HR', 'POSITION', 'CREATE', 'Create job positions'),
    ('HR', 'POSITION', 'READ', 'View job positions'),
    ('HR', 'POSITION', 'UPDATE', 'Modify job positions'),
    ('HR', 'POSITION', 'DELETE', 'Delete job positions'),
    ('HR', 'LEAVE', 'CREATE', 'Submit employee leave requests'),
    ('HR', 'LEAVE', 'READ', 'View employee leave requests'),
    ('HR', 'LEAVE', 'UPDATE', 'Modify leave requests'),
    ('HR', 'LEAVE', 'DELETE', 'Delete leave requests'),
    ('HR', 'LEAVE', 'APPROVE', 'Approve or reject employee leave requests'),
    ('HR', 'ATTENDANCE', 'CREATE', 'Record employee attendance'),
    ('HR', 'ATTENDANCE', 'READ', 'View employee attendance'),
    ('HR', 'ATTENDANCE', 'UPDATE', 'Modify employee attendance'),
    ('HR', 'ATTENDANCE', 'DELETE', 'Delete employee attendance'),
    ('HR', 'PAYROLL', 'CREATE', 'Create payroll records'),
    ('HR', 'PAYROLL', 'READ', 'View payroll information'),
    ('HR', 'PAYROLL', 'UPDATE', 'Modify payroll records'),
    ('HR', 'PAYROLL', 'DELETE', 'Delete payroll records'),
    ('HR', 'MODULE', 'CRUD', 'Full access to the Human Resources module'),
    ('INVENTORY', 'PRODUCT', 'CREATE', 'Create inventory products'),
    ('INVENTORY', 'PRODUCT', 'READ', 'View inventory products'),
    ('INVENTORY', 'PRODUCT', 'UPDATE', 'Modify inventory products'),
    ('INVENTORY', 'PRODUCT', 'DELETE', 'Delete inventory products'),
    ('INVENTORY', 'CATEGORY', 'CREATE', 'Create product categories'),
    ('INVENTORY', 'CATEGORY', 'READ', 'View product categories'),
    ('INVENTORY', 'CATEGORY', 'UPDATE', 'Modify product categories'),
    ('INVENTORY', 'CATEGORY', 'DELETE', 'Delete product categories'),
    ('INVENTORY', 'WAREHOUSE', 'CREATE', 'Create warehouses'),
    ('INVENTORY', 'WAREHOUSE', 'READ', 'View warehouses'),
    ('INVENTORY', 'WAREHOUSE', 'UPDATE', 'Modify warehouses'),
    ('INVENTORY', 'WAREHOUSE', 'DELETE', 'Delete warehouses'),
    ('INVENTORY', 'STOCK', 'READ', 'View current stock levels'),
    ('INVENTORY', 'STOCK', 'ADJUST', 'Adjust stock quantities'),
    ('INVENTORY', 'STOCK_MOVEMENT', 'CREATE', 'Record stock movements'),
    ('INVENTORY', 'STOCK_MOVEMENT', 'READ', 'View stock movement history'),
    ('INVENTORY', 'SUPPLIER', 'CREATE', 'Create suppliers'),
    ('INVENTORY', 'SUPPLIER', 'READ', 'View supplier information'),
    ('INVENTORY', 'SUPPLIER', 'UPDATE', 'Modify supplier information'),
    ('INVENTORY', 'SUPPLIER', 'DELETE', 'Delete suppliers'),
    ('INVENTORY', 'MODULE', 'CRUD', 'Full access to the Inventory module')
) AS p(module_key, resource, action, description) ON p.module_key = m.module_key
ON CONFLICT (code) DO NOTHING;
