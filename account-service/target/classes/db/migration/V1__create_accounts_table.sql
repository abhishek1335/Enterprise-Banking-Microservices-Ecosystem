CREATE TABLE accounts (
    id              UUID PRIMARY KEY,
    user_id         UUID NOT NULL,
    account_number  VARCHAR(20) NOT NULL,
    account_type    VARCHAR(30) NOT NULL,
    balance         DECIMAL(19, 4) NOT NULL DEFAULT 0,
    currency        VARCHAR(3) NOT NULL DEFAULT 'USD',
    status          VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    version         BIGINT NOT NULL DEFAULT 0,
    created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_accounts_number UNIQUE (account_number)
);

CREATE INDEX idx_accounts_user_id ON accounts (user_id);
