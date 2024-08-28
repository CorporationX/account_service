CREATE TABLE payment_account (
    id bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
    number varchar(20) UNIQUE NOT NULL,
    owner_id bigint NOT NULL,
    type smallint NOT NULL,
    currency smallint NOT NULL,
    status smallint NOT NULL,
    created_at timestamptz DEFAULT current_timestamp,
    updated_at timestamptz DEFAULT current_timestamp,
    deleted_at timestamptz,
    version int
);