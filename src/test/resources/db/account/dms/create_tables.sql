drop table if exists public.reserve;
DROP TABLE IF EXISTS public.balance;
DROP TABLE IF EXISTS public.reserve;
DROP TABLE IF EXISTS public.accounts;
DROP TABLE IF EXISTS public.accounts_owners;


CREATE TABLE IF NOT EXISTS public.accounts_owners
(
    id         bigserial PRIMARY KEY,
    owner_id   bigint   NOT NULL,
    owner_type smallint NOT NULL,
    UNIQUE (owner_id, owner_type)
);

CREATE TABLE IF NOT EXISTS public.accounts
(
    id             bigserial PRIMARY KEY,
    account_number varchar(20) NOT NULL UNIQUE CHECK (length(account_number) >= 12),
    owner_id       bigint      NOT NULL,
    account_type   smallint    NOT NULL,
    currency       smallint    NOT NULL,
    status         smallint    NOT NULL DEFAULT 0,
    closed_at      timestamptz,
    version        bigint      NOT NULL DEFAULT 0,
    created_at     timestamptz          DEFAULT current_timestamp,
    updated_at     timestamptz          DEFAULT current_timestamp,
    CONSTRAINT fk_owner_id
        FOREIGN KEY (owner_id) REFERENCES public.accounts_owners (id)
);

CREATE TABLE IF NOT EXISTS balance
(
    id             BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    account_id     BIGINT         NOT NULL REFERENCES accounts (id),
    auth_balance   NUMERIC(15, 2) NOT NULL,
    actual_balance NUMERIC(15, 2) NOT NULL,
    created_at     timestamptz DEFAULT CURRENT_TIMESTAMP,
    updated_at     timestamptz DEFAULT CURRENT_TIMESTAMP,
    version        BIGINT      DEFAULT 0
);

create table public.reserve (
    id bigint primary key generated always as identity,
    request_id bigint not null unique,
    sender_account_id bigint not null,
    receiver_account_id bigint not null,
    amount numeric(16, 2) not null,
    status varchar(64) not null,
    clear_scheduled_at timestamp not null,
    created_at timestamp not null default current_timestamp,
    updated_at timestamp not null default current_timestamp,

    foreign key (sender_account_id) references public.accounts(id),
    foreign key (receiver_account_id) references public.accounts(id)
);

insert into public.accounts_owners(owner_id,  owner_type)
values (1, 0), (2, 0);

insert into public.accounts(
    account_number,  owner_id,   account_type,  currency,
    status,          closed_at,  version
)
values (1111111111111111, (select lead(id, 1) over(order by id desc) from public.accounts_owners fetch first 1 rows only)
        , 0, 0, 0, null, 1),
    (2222222222222222, (select max(id) from public.accounts_owners), 0, 0, 0, null, 1);

insert into public.balance (account_id,  auth_balance,  actual_balance, version)
values((select lead(id, 1) over(order by id desc) from public.accounts fetch first 1 rows only), 1000, 1000, 1),
    ((select max(id) from public.accounts), 500, 500, 1);