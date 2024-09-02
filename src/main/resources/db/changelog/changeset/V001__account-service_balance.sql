create table balance
(
    id                    bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
    authorization_balance DOUBLE PRECISION NOT NULL DEFAULT 0.0,
    balance               DOUBLE PRECISION NOT NULL DEFAULT 0.0,
    created_at            timestamptz               DEFAULT current_timestamp,
    updated_at            timestamptz               DEFAULT current_timestamp,
    version               bigint           NOT NULL DEFAULT 0
)
