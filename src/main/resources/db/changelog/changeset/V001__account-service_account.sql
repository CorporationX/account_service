-- Write your sql migration here!
CREATE TABLE account (
    id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
    number VARCHAR(20) NOT NULL UNIQUE,
    owner_type SMALLINT NOT NULL,
    owner_id BIGINT NOT NULL,
    account_type SMALLINT NOT NULL,
    currency SMALLINT NOT NULL,
    account_status SMALLINT NOT NULL,
    create_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    close_at TIMESTAMP,
    version INT NOT NULL DEFAULT 0
    )
CREATE INDEX owner_idx ON account (owner_id)