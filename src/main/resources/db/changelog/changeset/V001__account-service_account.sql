create table if not exists account
(
    id             bigserial primary key,
    account_number varchar(20) not null check ( length(account_number) between 12 and 20),
    owner_id       bigint      not null,
    owner_type     varchar(32) not null,
    account_type   varchar(32) not null default 0,
    currency       varchar(32) not null,
    account_status varchar(32) not null,
    created_at     timestamp            default current_timestamp,
    updated_at     timestamp            default current_timestamp,
    closed_at      timestamp,
    version        bigint      not null default 1,
    unique (account_number)
);

create index idx_account_number on account (account_number);
create index idx_owner_id_type on account (owner_id, owner_type);