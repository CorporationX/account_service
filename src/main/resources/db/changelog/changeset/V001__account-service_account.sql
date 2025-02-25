-- Write your sql migration here!
CREATE TYPE owner_type AS ENUM ('user', 'project');
CREATE TYPE status_type AS ENUM ('active', 'frozen', 'closed');
CREATE TYPE currency_type AS ENUM ('USD', 'EUR', 'RUB');

CREATE TABLE IF NOT EXISTS account
(
    id bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
    account varchar(20) NOT NULL UNIQUE CHECK (example_column ~ '^[0-9]{12,20}$'),
    balance DECIMAL(15,2) DEFAULT 0.00,
    owner owner_type NOT NULL,
    type varchar(50) NOT NULL,
    currency currency_type NOT NULL,
    status status_type NOT NULL,
    created_at timestamptz DEFAULT current_timestamp,
    updated_at timestamptz DEFAULT current_timestamp,
    closed_at timestamptz DEFAULT NULL,
    account_version varchar(20)
);

comment on table account is 'Платежный счет';
comment on column account is 'Номер счета';
comment on column balance is 'баланс счета';
comment on column owner is 'Владелец: пользователь или проект';
comment on column type is 'тип счета';
comment on column currency is 'валюта счета';
comment on column status is 'статус: действующий, замороженный или закрытый';
comment on column created_at is 'время создания счета';
comment on column updated_at is 'время изменения счета';
comment on column closed_at is 'время закрытия счета';
comment on column account_version is 'версия счета';