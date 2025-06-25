CREATE TABLE accounts (
    id bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
    number varchar(32) UNIQUE NOT NULL,
    owner_type varchar(32) NOT NULL,
    owner_id bigint NOT NULL,
    account_type varchar(64) NOT NULL,
    currency varchar(4) NOT NULL,
    status varchar(64) NOT NULL,
    created_at timestamptz DEFAULT current_timestamp,
    updated_at timestamptz DEFAULT current_timestamp,
    closed_at timestamptz DEFAULT NULL,
    version int DEFAULT 1,
    balance decimal,
    description varchar(512)
);