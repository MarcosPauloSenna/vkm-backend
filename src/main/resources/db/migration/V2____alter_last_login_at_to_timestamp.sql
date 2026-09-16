UPDATE users
SET last_login_at = NULL;

ALTER TABLE users
    ALTER COLUMN last_login_at
        TYPE TIMESTAMP WITHOUT TIME ZONE
        USING NULL;