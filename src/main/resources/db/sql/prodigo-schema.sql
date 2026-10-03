CREATE TABLE IF NOT EXISTS "tenants" (
    "id" UUID NOT NULL PRIMARY KEY UNIQUE,
    "name" VARCHAR(255) NOT NULL,
    "slug" VARCHAR(255) NOT NULL UNIQUE,
    "country" VARCHAR(50) NOT NULL,
    "status" VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    "created_at" TIMESTAMP NOT NULL default current_timestamp,
    "updated_at" TIMESTAMP NOT NULL,
    "created_by" VARCHAR(100) NOT NULL,
    "updated_by" VARCHAR(100) NOT NULL
);

CREATE TABLE IF NOT EXISTS "tenant_entitlements" (
    "id" UUID NOT NULL PRIMARY KEY UNIQUE,
    "tenant_id" UUID NOT NULL UNIQUE,
    "max_users" INT NOT NULL DEFAULT 5,
    "max_storage_gb" INT NOT NULL DEFAULT 5,
    "max_products" BIGINT NOT NULL DEFAULT 20,
    "created_at" TIMESTAMP NOT NULL DEFAULT current_timestamp,
    "updated_at" TIMESTAMP NOT NULL,
    "created_by" VARCHAR(100) NOT NULL,
    "updated_by" VARCHAR(100) NOT NULL
);

ALTER TABLE "tenant_entitlements"
    ADD CONSTRAINT fk_tenant_entitlements_tenants
        FOREIGN KEY ("tenant_id") REFERENCES "tenants"("id")
            ON DELETE CASCADE;

CREATE TABLE IF NOT EXISTS "modules" (
    "id" UUID NOT NULL PRIMARY KEY ,
    "name" VARCHAR(150) NOT NULL UNIQUE,
    "module_key" VARCHAR(255) NOT NULL UNIQUE,
    "description" VARCHAR(255),
    "price" DECIMAL(10, 2) NOT NULL DEFAULT 0,
    "currency" VARCHAR(3) NOT NULL DEFAULT 'XAF',
    "is_active" BOOLEAN NOT NULL DEFAULT true,
    "created_at" TIMESTAMP NOT NULL default current_timestamp,
    "updated_at" TIMESTAMP NOT NULL,
    "created_by" VARCHAR(100) NOT NULL,
    "updated_by" VARCHAR(100) NOT NULL
);

CREATE TABLE IF NOT EXISTS "module_subscriptions" (
    "id" UUID NOT NULL PRIMARY KEY,
    "tenant_id" UUID NOT NULL,
    "module_id" UUID NOT NULL,
    "status" VARCHAR(30) NOT NULL DEFAULT 'ACTIVE',
    "is_free" BOOLEAN NOT NULL DEFAULT false,
    "price" DECIMAL(10,2) NOT NULL DEFAULT 0,
    "currency" VARCHAR(3) NOT NULL DEFAULT 'XAF',
    "activated_at" TIMESTAMP NOT NULL,
    "expires_at" TIMESTAMP NOT NULL,
    "created_at" TIMESTAMP NOT NULL default current_timestamp,
    "updated_at" TIMESTAMP NOT NULL,
    "created_by" VARCHAR(100) NOT NULL,
    "updated_by" VARCHAR(100) NOT NULL,
    UNIQUE("tenant_id", "module_id")
);

ALTER TABLE "module_subscriptions"
    ADD CONSTRAINT fk_modulesubscription_module
        FOREIGN KEY ("module_id") REFERENCES "modules"("id")
            ON DELETE CASCADE;

ALTER TABLE "module_subscriptions"
    ADD CONSTRAINT fk_modulesubscription_tenant
        FOREIGN KEY ("tenant_id") REFERENCES "tenants"("id")
            ON DELETE CASCADE;


CREATE TABLE IF NOT EXISTS "users" (
    "id" UUID NOT NULL PRIMARY KEY,
    "username" VARCHAR(20) NOT NULL UNIQUE,
    "tenant_id" UUID,
    "email" VARCHAR(150) NOT NULL UNIQUE,
    "password" VARCHAR(255) NOT NULL,
    "first_name" VARCHAR(150) NOT NULL,
    "last_name" VARCHAR(150) NOT NULL,
    "status" VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    "is_super_admin" BOOLEAN NOT NULL DEFAULT false,
    "last_login" TIMESTAMP NOT NULL,
    "created_at" TIMESTAMP NOT NULL default current_timestamp,
    "updated_at" TIMESTAMP NOT NULL,
    "created_by" VARCHAR(100) NOT NULL,
    "updated_by" VARCHAR(100) NOT NULL
);

ALTER TABLE "users"
    ADD CONSTRAINT fk_user_tenant
        FOREIGN KEY ("tenant_id") REFERENCES "tenants"("id")
            ON DELETE CASCADE;


CREATE TABLE IF NOT EXISTS "roles" (
    "id" UUID PRIMARY KEY NOT NULL,
    "tenant_id" UUID,
    "name" VARCHAR(20) NOT NULL,
    "description" TEXT,
    "is_default" BOOLEAN NOT NULL DEFAULT false,
    "created_at" TIMESTAMP NOT NULL default current_timestamp,
    "updated_at" TIMESTAMP NOT NULL,
    "created_by" VARCHAR(100) NOT NULL,
    "updated_by" VARCHAR(100) NOT NULL
);

ALTER TABLE "roles"
    ADD CONSTRAINT fk_role_tenant
        FOREIGN KEY ("tenant_id") REFERENCES "tenants"("id")
            ON DELETE CASCADE ;

-- NOTE: here tenantId can be null meaning if it happens to be null,
-- this will be considered as a role of the system itself
CREATE TABLE IF NOT EXISTS "user_roles" (
    "id" UUID PRIMARY KEY NOT NULL,
    "user_id" UUID NOT NULL,
    "role_id" UUID NOT NULL,
    "tenant_id" UUID,
    "assigned_by" VARCHAR(100) NOT NULL,
    "assigned_at" TIMESTAMP NOT NULL DEFAULT current_timestamp,
    "expires_at" TIMESTAMP,
    UNIQUE("user_id", "role_id")
);

ALTER TABLE "user_roles"
    ADD CONSTRAINT fk_userrole_user
        FOREIGN KEY ("user_id") REFERENCES "users"("id")
            ON DELETE CASCADE;

ALTER TABLE "user_roles"
    ADD CONSTRAINT fk_userrole_role
        FOREIGN KEY ("role_id") REFERENCES "roles"("id")
            ON DELETE CASCADE;


CREATE TABLE IF NOT EXISTS "permissions" (
    "id" UUID PRIMARY KEY NOT NULL,
    "code" VARCHAR(50) NOT NULL UNIQUE,
    "description" TEXT,
    "action" VARCHAR(50) NOT NULL,
    "resource" VARCHAR(100) NOT NULL,
    "module_id" UUID NOT NULL,
    "created_at" TIMESTAMP NOT NULL default current_timestamp,
    "updated_at" TIMESTAMP NOT NULL,
    "created_by" VARCHAR(100) NOT NULL,
    "updated_by" VARCHAR(100) NOT NULL,
    UNIQUE("code","resource")
);

ALTER TABLE "permissions"
    ADD CONSTRAINT fk_permission_module
        FOREIGN KEY("module_id") REFERENCES "modules"("id")
            ON DELETE CASCADE;


CREATE TABLE IF NOT EXISTS "role_permissions" (
    "id" UUID PRIMARY KEY NOT NULL,
    "role_id" UUID NOT NULL,
    "permission_id" UUID NOT NULL,
    "granted_at" TIMESTAMP NOT NULL default current_timestamp,
    "granted_by" VARCHAR(100) NOT NULL,
    UNIQUE ("role_id", "permission_id")
);

ALTER TABLE "role_permissions"
    ADD CONSTRAINT fk_rolepermission_role
        FOREIGN KEY ("role_id") REFERENCES "roles"("id")
            ON DELETE CASCADE;

ALTER TABLE "role_permissions"
    ADD CONSTRAINT fk_rolepermission_permission
        FOREIGN KEY("permission_id") REFERENCES "permissions"("id")
            ON DELETE CASCADE;

CREATE TABLE IF NOT EXISTS "refresh_tokens" (
    "id" UUID PRIMARY KEY NOT NULL,
    "token" TEXT NOT NULL,
    "user_id" UUID NOT NULL,
    "expiry_date" TIMESTAMP NOT NULL,
    "is_revoked" BOOLEAN NOT NULL DEFAULT false,
    "created_at" TIMESTAMP NOT NULL DEFAULT current_timestamp
);

ALTER TABLE "refresh_tokens"
    ADD CONSTRAINT fk_refreshtoken_user
        FOREIGN KEY("user_id") REFERENCES "users"("id")
            ON DELETE CASCADE;