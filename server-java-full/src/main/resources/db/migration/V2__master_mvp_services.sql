ALTER TABLE app_account ADD COLUMN IF NOT EXISTS bio VARCHAR(280) NOT NULL DEFAULT '';
ALTER TABLE app_account ADD COLUMN IF NOT EXISTS avatar_url VARCHAR(2048);

CREATE TABLE IF NOT EXISTS app_session (
    id UUID PRIMARY KEY,
    account_id UUID NOT NULL REFERENCES app_account(id) ON DELETE CASCADE,
    token_hash VARCHAR(64) NOT NULL UNIQUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    expires_at TIMESTAMPTZ NOT NULL,
    revoked_at TIMESTAMPTZ
);
CREATE INDEX IF NOT EXISTS idx_app_session_account ON app_session(account_id);
CREATE INDEX IF NOT EXISTS idx_app_session_expiry ON app_session(expires_at);

CREATE TABLE IF NOT EXISTS email_verification_code (
    id UUID PRIMARY KEY,
    account_id UUID NOT NULL REFERENCES app_account(id) ON DELETE CASCADE,
    code_hash VARCHAR(255) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    expires_at TIMESTAMPTZ NOT NULL,
    used_at TIMESTAMPTZ,
    attempts INTEGER NOT NULL DEFAULT 0
);
CREATE INDEX IF NOT EXISTS idx_verification_account_created ON email_verification_code(account_id, created_at DESC);
