CREATE SEQUENCE account_id_seq START 0;
CREATE TABLE account (
    id BIGINT PRIMARY KEY DEFAULT nextval('account_id_seq'),
    number VARCHAR(20) NOT NULL,
    owner_type VARCHAR(16) NOT NULL,
    owner_id bigint NOT NULL,
    type VARCHAR(32) NOT NULL,
    currency VARCHAR(16) NOT NULL,
    status VARCHAR(16) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    closed_at TIMESTAMP,
    version VARCHAR(16)
) AUTO_INCREMENT = 0;

CREATE INDEX owner_idx ON account(number)