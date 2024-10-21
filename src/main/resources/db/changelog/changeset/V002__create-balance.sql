CREATE TABLE IF NOT EXISTS balance(
    id bigint PRIMARY KEY,
    account_id BIGSERIAL,
    cur_auth_balance real,
    cur_fact_balance real,
    created_at date,
    updated_at date,
    constraint bank_account
    foreign key(account_id) references account(id),
    version int NOT NULL
)