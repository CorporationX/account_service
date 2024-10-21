CREATE TABLE IF NOT EXISTS balance(
    id bigint PRIMARY KEY,
    account_id BIGSERIAL,
    cur_auth_balance int,
    cur_fact_balance int,
    created_at date,
    updated_at date,
    constraint bank_account
    foreign key(account_id) references account(id),
    version int NOT NULL
)