create table savings_account (
    id serial primary key,
    tariff_history text,
    last_interest_calculated_date date,
    version bigint not null default 1,
    created_at timestamp not null default current_timestamp,
    updated_at timestamp not null default current_timestamp
);