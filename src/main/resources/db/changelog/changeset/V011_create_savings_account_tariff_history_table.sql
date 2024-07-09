create table savings_account_tariff_history (
    id serial primary key,
    savings_account_id bigint not null references savings_account(id), -- исправленная строка
    tariff_id bigint not null references tariff(id),
    start_date timestamp not null default current_timestamp,
    end_date timestamp
);