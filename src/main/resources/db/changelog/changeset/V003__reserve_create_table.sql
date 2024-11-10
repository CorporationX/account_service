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