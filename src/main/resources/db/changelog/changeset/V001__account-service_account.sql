CREATE TABLE account (
    id bigserial PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
    number varchar(20) UNIQUE NOT NULL,
    owner_type varchar(32) NOT NULL,
    owner_id bigint NOT NULL,
    type varchar(64) NOT NULL,
    currency varchar(4) NOT NULL,
    status varchar(32) NOT NULL,
    created_at timestampz NOT NULL DEFAULT now(),
    updated_at timestampz not null,
    closed_at timestampz,
    version bigint NOT NULL,

    CREATE INDEX idx_account_owner ON account(owner_type, owner_id);
)
