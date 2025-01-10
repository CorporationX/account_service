create table if not exists public.savings_account (
    id bigserial primary key,
    account_id bigint not null,
    last_interest_calculation_date timestamp null,
    version int default 1 not null,
    created_at timestamp default current_timestamp not null,
    updated_at timestamp default current_timestamp not null,
    constraint fk_account_id foreign key(account_id) references public.account(id) on delete cascade
);

create table if not exists public.tariff (
    id bigserial primary key,
    type varchar(128) not null,
    rate decimal(8, 2) not null,
    created_at timestamp default current_timestamp not null,
    updated_at timestamp default current_timestamp not null
);

create table if not exists public.tariff_history (
    id bigserial primary key,
    savings_account_id bigint not null,
    tariff_id bigint not null,
    applied_date timestamp default current_timestamp not null,
    removed_date timestamp,
    constraint fk_savings_account foreign key(savings_account_id) references public.savings_account(id) on delete cascade,
    constraint fk_tariff foreign key(tariff_id) references public.tariff(id) on delete cascade
);

create table if not exists public.rate_history (
    id bigserial primary key,
    rate decimal not null,
    tariff_id bigint not null,
    start_date timestamp default current_timestamp not null,
    end_date timestamp,
    constraint fk_tariff_idx foreign key(tariff_id) references public.tariff(id) on delete cascade
);

create index idx_account_id on public.savings_account(account_id);
create index idx_savings_account_id on public.tariff_history(savings_account_id);
create index idx_tariff_applied_date on public.tariff_history(applied_date);