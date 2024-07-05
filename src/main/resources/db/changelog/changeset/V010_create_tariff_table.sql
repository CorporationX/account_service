create table tariff (
    id bigint primary key,
    name varchar(64) not null,
    rate_history text,
    created_at timestamp not null default current_timestamp,
    updated_at timestamp not null default current_timestamp
);