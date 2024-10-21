CREATE TABLE IF NOT EXISTS balance(
    id bigint PRIMARY KEY,
    cur_auth_balance int,
    cur_fact_balance int,
    created_at date,
    updated_at date,
    version int NOT NULL
)