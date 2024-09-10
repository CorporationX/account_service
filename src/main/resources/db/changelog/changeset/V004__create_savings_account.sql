create table savings_account (
    id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
    account_id BIGINT NOT NULL UNIQUE references account(id),
    tariff_history jsonb not null,
    last_success_percent_date date DEFAULT CURRENT_DATE,
    created_at timestamptz DEFAULT CURRENT_TIMESTAMP,
    updated_at timestamptz DEFAULT CURRENT_TIMESTAMP,
    version bigint not null
);

create table tariff (
    id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
    type varchar(64) not null unique,
    percent_tariff_history jsonb not null
);

INSERT INTO tariff(type, percent_tariff_history) VALUES
    ('PROMO', '[15]'),
    ('SUBSCRIBE', '[10]'),
    ('BASE', '[5]')
