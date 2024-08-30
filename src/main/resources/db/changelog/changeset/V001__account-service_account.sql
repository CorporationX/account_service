CREATE TABLE account
(
    id                UUID         NOT NULL PRIMARY KEY,
    account_number    VARCHAR(20)  NOT NULL UNIQUE,
    owner_id          BIGINT       NOT NULL,
    owner_type        VARCHAR(255) NOT NULL,
    account_type      VARCHAR(255) NOT NULL,
    currency          VARCHAR(255) NOT NULL,
    status            VARCHAR(255) NOT NULL,
    balance           DECIMAL(19, 4) DEFAULT 0,
    transaction_limit DECIMAL(19, 4) DEFAULT 0,
    version           BIGINT       NOT NULL,
    created_at        timestamptz    DEFAULT current_timestamp,
    updated_at        timestamptz    DEFAULT current_timestamp,
    closed_at         timestamptz
);

CREATE INDEX idx_account_owner ON account (owner_id);

CREATE TABLE account_number_pool
(
    id             UUID               NOT NULL PRIMARY KEY,
    account_number VARCHAR(20) UNIQUE NOT NULL
);


