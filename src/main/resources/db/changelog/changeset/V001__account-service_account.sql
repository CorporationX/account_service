CREATE TABLE account (
    id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
    number VARCHAR(20) CHECK(LENGTH(number) >= 12) NOT NULL UNIQUE,
    owner_id BIGINT NOT NULL,
    type VARCHAR(16) NOT NULL DEFAULT 'CHECKING',
    currency VARCHAR(3) NOT NULL DEFAULT 'USD',
    status VARCHAR(8) NOT NULL DEFAULT 'CURRENT',
    balance NUMERIC,
    created_at timestamptz DEFAULT current_timestamp,
    updated_at timestamptz DEFAULT current_timestamp,
    closed_at timestamptz DEFAULT current_timestamp,
    version INT NOT NULL
);

CREATE INDEX owner_id_idx ON account (owner_id);
