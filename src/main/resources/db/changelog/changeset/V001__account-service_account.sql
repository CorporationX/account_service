create table if not exists public.account
(
    id         bigserial
    primary key,
    closed_at  timestamp(6),
    accounttype varchar(255),
    created_at timestamp(6),
    currency   varchar(255),
    number     varchar(20) not null
    unique,
    status     varchar(255),
    updated_at timestamp(6),
    version     integer
    );

alter table public.account
    owner to "user";