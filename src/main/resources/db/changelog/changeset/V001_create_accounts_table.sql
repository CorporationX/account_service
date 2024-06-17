CREATE TABLE accounts (
    id serial primary key,
    number varchar not null check(length(number) between 12 and 20),
    owner_id bigint not null,
    account_type varchar(32) not null,
    currency varchar(20) not null,
    account_status varchar(32) not null,
    balance_id bigint not null,
    created_at timestamptz default current_timestamp,
    updatedAt timestamptz default current_timestamp,
    closedAt timestamptz,
    version bigint not null default 1
);

create index account_owner_index on accounts(owner_id);