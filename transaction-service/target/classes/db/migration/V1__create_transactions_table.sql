CREATE TABLE transactions (
    id                  UUID PRIMARY KEY,
    reference_number    VARCHAR(30) NOT NULL,
    from_account_id     UUID NOT NULL,
    to_account_id       UUID NOT NULL,
    initiated_by        UUID NOT NULL,
    amount              DECIMAL(19, 4) NOT NULL,
    currency            VARCHAR(3) NOT NULL DEFAULT 'USD',
    transaction_type    VARCHAR(30) NOT NULL,
    status              VARCHAR(20) NOT NULL,
    description         VARCHAR(255),
    created_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_transactions_reference UNIQUE (reference_number)
);

CREATE INDEX idx_transactions_from_account ON transactions (from_account_id);
CREATE INDEX idx_transactions_to_account ON transactions (to_account_id);
CREATE INDEX idx_transactions_initiated_by ON transactions (initiated_by);
