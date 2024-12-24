create table if not exists public.balance_audit
(
    id                    bigserial
    primary key,
    authorization_balance numeric(38, 2),
    created_at            timestamp(6),
    current_balance       numeric(38, 2),
    transaction_id        varchar(255),
    version_id            bigint,
    account_id            bigint
    constraint fkoedqfubv86ofodiw1mb0dp6t9
    references public.account
    );

alter table public.balance_audit
    owner to "user";

create index if not exists idx_balance_audit_id
    on public.balance_audit (id);