ALTER TABLE refresh_tokens
    ALTER COLUMN expires_at SET NOT NULL,
    ALTER COLUMN created_at SET NOT NULL;