CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE currency (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    create_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL,
    name VARCHAR(64) NOT NULL,
    iso_code VARCHAR(8) NOT NULL UNIQUE,
    symbol VARCHAR(4)
);

CREATE TABLE account (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    create_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL,

    number VARCHAR(128) NOT NULL UNIQUE,
    user_id BIGINT,
    project_id BIGINT,

    owner_type VARCHAR(32) NOT NULL,
    type VARCHAR(32) NOT NULL,
    status VARCHAR(32) NOT NULL,

    currency_id UUID NOT NULL,
    close_at TIMESTAMP,

    CONSTRAINT fk_account_currency FOREIGN KEY (currency_id) REFERENCES currency(id),
    CONSTRAINT chk_owner_presence
    CHECK (
        (user_id IS NOT NULL AND project_id IS NULL) OR
        (user_id IS NULL AND project_id IS NOT NULL)
    )
);