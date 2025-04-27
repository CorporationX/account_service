CREATE TABLE account (
    id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
    number VARCHAR(20) NOT NULL
    CHECK(
    number ~ '^[0-9]{12,20}$' ),
    owner_id  BIGINT NOT NULL,
    owner_type VARCHAR(64) NOT NULL,
    type VARCHAR(64)  NOT NULL,
    currency VARCHAR(3) NOT NULL,
    status varchar(64) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    closed_at TIMESTAMP WITH TIME ZONE,
    version INTEGER DEFAULT 0 NOT NULL
);
CREATE INDEX idx_account_owner ON account(owner_type, owner_id);