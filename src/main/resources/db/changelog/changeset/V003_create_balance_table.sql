create table balance (
    id serial primary key,
    current_balance numeric(30, 2) not null default 0,
    updatedAt timestamptz default current_timestamp,
    account_id bigint references accounts(id),
    version bigint not null default 1
);