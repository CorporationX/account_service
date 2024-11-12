CREATE TABLE tariff (
    id          BIGINT      NOT NULL PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
    tariff_type VARCHAR(32) NOT NULL UNIQUE
);

CREATE TABLE tariff_rate (
    id         BIGINT           NOT NULL PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
    tariff_id  BIGINT           NOT NULL,
    rate       DOUBLE PRECISION NOT NULL,
    created_at TIMESTAMP                  DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_tariff_rate_tariff_id FOREIGN KEY (tariff_id) REFERENCES tariff (id)
);

CREATE TABLE savings_account (
    id                    UUID   NOT NULL PRIMARY KEY,
    account_id            UUID   NOT NULL,
    last_calculation_date DATE,
    version               BIGINT NOT NULL DEFAULT 0,
    created_at            TIMESTAMP       DEFAULT CURRENT_TIMESTAMP,
    updated_at            TIMESTAMP       DEFAULT CURRENT_TIMESTAMP,

     CONSTRAINT fk_savings_account_account_id FOREIGN KEY (account_id) REFERENCES account (id)
);

CREATE TABLE tariff_to_savings_account_binding (
    id                 UUID NOT NULL PRIMARY KEY,
    tariff_id          BIGINT NOT NULL,
    savings_account_id UUID NOT NULL,
    created_at         TIMESTAMP      DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_tariff_to_savings_account_binding_tariff_id FOREIGN KEY (tariff_id) REFERENCES tariff (id),
    CONSTRAINT fk_tariff_to_savings_account_binding_savings_account_id FOREIGN KEY (savings_account_id) REFERENCES savings_account (id)
);