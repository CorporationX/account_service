create table account_owner (
    id bigint primary key generated always as identity,
    owner_id bigint not null,
    owner_type varchar(16) not null,
    created_at timestamptz default current_timestamp not null,

    unique (owner_id, owner_type)
);

create table account (
    id bigserial primary key,
    account_number varchar(20) not null unique,
    type varchar(32) not null,
    currency varchar(3) not null,
    status varchar(16) not null,
    created_at timestamptz default current_timestamp not null,
    updated_at timestamptz default current_timestamp not null,
    closed_at timestamptz,
    version int default 1 not null,
    owner_id bigint not null,

    constraint fk_owner_id foreign key (owner_id)
        references account_owner (id) on delete cascade
);

create index if not exists idx_account_owner_owner
    on account_owner (owner_id, owner_type);
