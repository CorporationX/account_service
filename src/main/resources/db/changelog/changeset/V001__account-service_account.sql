CREATE SEQUENCE account_id_seq START 1;
CREATE TABLE account (
    id BIGINT PRIMARY KEY DEFAULT nextval('account_id_seq'),
    number VARCHAR(20) NOT NULL,
    owner_type VARCHAR(16) NOT NULL,
    owner_id BIGINT NOT NULL,
    type VARCHAR(32) NOT NULL,
    currency VARCHAR(16) NOT NULL,
    status VARCHAR(16) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    closed_at TIMESTAMP,
    version VARCHAR(16)
);

CREATE INDEX number_idx ON account(number);