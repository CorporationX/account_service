-- Write your sql migration here!
CREATE TYPE owner_type AS ENUM ('user', 'project');
CREATE TYPE status_type AS ENUM ('active', 'frozen', 'closed');
CREATE TYPE currency_type AS ENUM ('USD', 'EUR', 'RUB');

CREATE TABLE IF NOT EXISTS accounts
(
    id              bigint          PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
    account         varchar(20)     NOT NULL UNIQUE CHECK (account ~ '^[0-9]{12,20}$'),
    balance         DECIMAL(15,2)   DEFAULT 0.00,
    owner           owner_type      NOT NULL,
    type            varchar(50)     NOT NULL,
    currency        currency_type   NOT NULL,
    status          status_type     NOT NULL,
    created_at      timestamptz     DEFAULT current_timestamp,
    updated_at      timestamptz     DEFAULT current_timestamp,
    closed_at       timestamptz     DEFAULT NULL,
    account_version varchar(20)
);

comment on table accounts is 'Платежный счет';
comment on column accounts.account is 'Номер счета';
comment on column accounts.balance is 'баланс счета';
comment on column accounts.owner is 'Владелец: пользователь или проект';
comment on column accounts.type is 'тип счета';
comment on column accounts.currency is 'валюта счета';
comment on column accounts.status is 'статус: действующий, замороженный или закрытый';
comment on column accounts.created_at is 'время создания счета';
comment on column accounts.updated_at is 'время изменения счета';
comment on column accounts.closed_at is 'время закрытия счета';
comment on column accounts.account_version is 'версия счета';