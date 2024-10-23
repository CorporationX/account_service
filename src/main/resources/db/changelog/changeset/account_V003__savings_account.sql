CREATE TABLE tariff (
    id bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
    tariff_type VARCHAR(32) NOT NULL
);

CREATE TABLE tariff_rate (
    id bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
    tariff_id bigint NOT NULL,
    rate DOUBLE PRECISION NOT NULL,
    created_at timestamptz DEFAULT current_timestamp,

    CONSTRAINT fk_tariff_rate_tariff_id FOREIGN KEY (tariff_id) REFERENCES tariff (id)
);

CREATE TABLE savings_account (
    id bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
    account_id bigint NOT NULL,
    last_calculation_date DATE,
    version bigint NOT NULL DEFAULT 0,
    created_at timestamptz DEFAULT current_timestamp,
    updated_at timestamptz DEFAULT current_timestamp,

     CONSTRAINT fk_savings_account_account_id FOREIGN KEY (account_id) REFERENCES account (id)
);

CREATE TABLE savings_account_tariff (
    id bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
    tariff_id bigint NOT NULL,
    savings_account_id bigint NOT NULL,
    created_at timestamptz DEFAULT current_timestamp,

    CONSTRAINT fk_savings_account_tariff_tariff_id FOREIGN KEY (tariff_id) REFERENCES tariff (id),
    CONSTRAINT fk_savings_account_tariff_savings_account_id FOREIGN KEY (savings_account_id) REFERENCES savings_account (id)
);