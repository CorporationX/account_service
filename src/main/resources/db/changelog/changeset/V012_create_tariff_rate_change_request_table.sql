create table tariff_rate_change_request (
    id serial primary key,
    tariff_id bigint not null,
    new_rate decimal(10,2) not null,
    requested_at timestamp not null,
    change_date timestamp not null,
    status varchar(20) not null
);