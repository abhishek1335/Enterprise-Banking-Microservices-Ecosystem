CREATE TABLE notification_logs (
    id              UUID PRIMARY KEY,
    user_id         UUID NOT NULL,
    channel         VARCHAR(20) NOT NULL,
    notification_type VARCHAR(40) NOT NULL,
    recipient       VARCHAR(255) NOT NULL,
    subject         VARCHAR(255),
    message_body    TEXT NOT NULL,
    status          VARCHAR(20) NOT NULL,
    reference_id    VARCHAR(64),
    created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_notification_logs_user_id ON notification_logs (user_id);
CREATE INDEX idx_notification_logs_reference ON notification_logs (reference_id);
