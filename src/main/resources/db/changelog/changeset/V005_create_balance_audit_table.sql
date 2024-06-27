create table balance_audit (
    id serial primary key,
    account_number varchar (20) not null,
    authorized_amount numeric (30, 2) not null default 0,
    current_amount numeric (30, 2) not null default 0,
    operation_id bigint,
    created_at timestamp not null default current_timestamp,
    balance_version bigint not null default o,
);

create index account_number_index on balance_audit (account_number);