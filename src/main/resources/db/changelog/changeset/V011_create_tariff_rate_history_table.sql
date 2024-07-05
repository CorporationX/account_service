create table tariff_rate_history (
    id serial primary key,
    tariff_id bigint not null references tariff(id),
    rate decimal (10,4) not null,
    created_at timestamp not null default current_timestamp
);