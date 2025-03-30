CREATE TABLE IF NOT EXISTS balance_audit
(
    id                      BIGINT         PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    initiator_id            BIGINT         NOT NULL,
    audit_event_type        VARCHAR(32)    NOT NULL,
    current_auth_amount     NUMERIC(19,2)  NOT NULL,
    previous_auth_amount    NUMERIC(19,2),
    current_fact_amount     NUMERIC(19,2)  NOT NULL,
    previous_fact_amount    NUMERIC(19,2),
    audit_status            VARCHAR(16)    NOT NULL,
    audited_at              TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_initiator_id FOREIGN KEY (initiator_id) REFERENCES users (id)
);

CREATE INDEX IF NOT EXISTS idx_fk_balance_audit_initiator_id ON balance_audit(initiator_id)
