create table payment_history (
    id serial primary key,
    requester_number varchar(32) not null,
    receiver_number varchar(32) not null,
    currency varchar(16) not null,
    amount numeric(30, 2) not null,
    type varchar(32) not null,
    external_payment_key bigint not null,
    idempotency_key varchar(128) not null unique
)

create index on payment_history(idempotency_key)