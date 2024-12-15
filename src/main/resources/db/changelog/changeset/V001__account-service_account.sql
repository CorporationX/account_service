create table if not exists public.account
(
    id         bigserial
    primary key,
    closed_at  timestamp(6),
    created_at timestamp(6),
    currency   smallint,
    number     varchar(20) not null
    constraint uk_dbfiubqahb32ns85k023gr6nn
    unique,
    status     smallint,
    updated_at timestamp(6),
    version    varchar(255)
    );

alter table public.account
    owner to "user";

