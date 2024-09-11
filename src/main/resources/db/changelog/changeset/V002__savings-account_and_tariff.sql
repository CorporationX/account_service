CREATE TABLE savings_account (
    id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    tariff_history VARCHAR(20) NOT NULL,
    account_id BIGINT,
    last_calculated_date TIMESTAMPTZ,
    version INT NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_account FOREIGN KEY (account_id) REFERENCES account(id)
);

CREATE TABLE tariff (
    id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    tariff_type VARCHAR(20) NOT NULL,
    stakes VARCHAR,
    savings_account_id BIGINT,

    CONSTRAINT fk_savings_account FOREIGN KEY (savings_account_id) REFERENCES savings_account(id)
)