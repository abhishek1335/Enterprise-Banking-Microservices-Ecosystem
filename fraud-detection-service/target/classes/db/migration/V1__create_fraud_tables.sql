CREATE TABLE fraud_transaction_log (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id         UUID NOT NULL,
    transaction_id  UUID NOT NULL,
    reference_number VARCHAR(64) NOT NULL,
    amount          NUMERIC(19, 4) NOT NULL,
    currency        VARCHAR(3) NOT NULL,
    processed_at    TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_fraud_tx_log_user_time ON fraud_transaction_log (user_id, processed_at DESC);

CREATE TABLE fraud_alerts (
    id               UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id          UUID NOT NULL,
    transaction_id   UUID NOT NULL,
    reference_number VARCHAR(64) NOT NULL,
    rule_code        VARCHAR(64) NOT NULL,
    severity         VARCHAR(16) NOT NULL,
    description      TEXT NOT NULL,
    amount           NUMERIC(19, 4) NOT NULL,
    currency         VARCHAR(3) NOT NULL,
    status           VARCHAR(16) NOT NULL DEFAULT 'OPEN',
    created_at       TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_fraud_alerts_user_created ON fraud_alerts (user_id, created_at DESC);
CREATE INDEX idx_fraud_alerts_reference ON fraud_alerts (reference_number);
