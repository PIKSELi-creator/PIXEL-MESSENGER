CREATE TABLE IF NOT EXISTS app_account (
    id UUID PRIMARY KEY,
    email VARCHAR(320) NOT NULL UNIQUE,
    username VARCHAR(32) NOT NULL UNIQUE,
    display_name VARCHAR(64) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    email_verified BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE TABLE IF NOT EXISTS contact_relation (
    id UUID PRIMARY KEY,
    owner_id UUID NOT NULL REFERENCES app_account(id) ON DELETE CASCADE,
    contact_id UUID NOT NULL REFERENCES app_account(id) ON DELETE CASCADE,
    state VARCHAR(24) NOT NULL DEFAULT 'PENDING',
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT contact_not_self CHECK (owner_id <> contact_id),
    CONSTRAINT contact_pair_unique UNIQUE (owner_id, contact_id)
);
CREATE TABLE IF NOT EXISTS chat_room (
    id UUID PRIMARY KEY,
    kind VARCHAR(16) NOT NULL,
    title VARCHAR(100),
    created_by UUID NOT NULL REFERENCES app_account(id),
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE TABLE IF NOT EXISTS chat_member (
    chat_id UUID NOT NULL REFERENCES chat_room(id) ON DELETE CASCADE,
    user_id UUID NOT NULL REFERENCES app_account(id) ON DELETE CASCADE,
    role VARCHAR(16) NOT NULL DEFAULT 'MEMBER',
    joined_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (chat_id, user_id)
);
CREATE TABLE IF NOT EXISTS chat_message (
    id UUID PRIMARY KEY,
    chat_id UUID NOT NULL REFERENCES chat_room(id) ON DELETE CASCADE,
    sender_id UUID NOT NULL REFERENCES app_account(id),
    payload TEXT NOT NULL,
    payload_type VARCHAR(24) NOT NULL DEFAULT 'TEXT',
    reply_to UUID REFERENCES chat_message(id),
    edited_at TIMESTAMPTZ,
    deleted_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_chat_message_history ON chat_message(chat_id, created_at DESC);
CREATE TABLE IF NOT EXISTS message_receipt (
    message_id UUID NOT NULL REFERENCES chat_message(id) ON DELETE CASCADE,
    user_id UUID NOT NULL REFERENCES app_account(id) ON DELETE CASCADE,
    delivered_at TIMESTAMPTZ,
    read_at TIMESTAMPTZ,
    PRIMARY KEY (message_id, user_id)
);
CREATE TABLE IF NOT EXISTS message_reaction (
    message_id UUID NOT NULL REFERENCES chat_message(id) ON DELETE CASCADE,
    user_id UUID NOT NULL REFERENCES app_account(id) ON DELETE CASCADE,
    reaction VARCHAR(32) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (message_id, user_id, reaction)
);
CREATE TABLE IF NOT EXISTS user_block (
    blocker_id UUID NOT NULL REFERENCES app_account(id) ON DELETE CASCADE,
    blocked_id UUID NOT NULL REFERENCES app_account(id) ON DELETE CASCADE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (blocker_id, blocked_id),
    CONSTRAINT block_not_self CHECK (blocker_id <> blocked_id)
);
CREATE TABLE IF NOT EXISTS user_report (
    id UUID PRIMARY KEY,
    reporter_id UUID NOT NULL REFERENCES app_account(id),
    reported_user_id UUID NOT NULL REFERENCES app_account(id),
    reason VARCHAR(64) NOT NULL,
    details VARCHAR(2000),
    status VARCHAR(24) NOT NULL DEFAULT 'OPEN',
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE TABLE IF NOT EXISTS attachment_metadata (
    id UUID PRIMARY KEY,
    owner_id UUID NOT NULL REFERENCES app_account(id),
    storage_key VARCHAR(512) NOT NULL,
    content_type VARCHAR(128) NOT NULL,
    size_bytes BIGINT NOT NULL,
    sha256 VARCHAR(64) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);
