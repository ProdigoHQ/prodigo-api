-- A user who has never logged in must not have a login timestamp.
ALTER TABLE "users"
    ALTER COLUMN "last_login" DROP NOT NULL;
