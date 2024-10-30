CREATE TABLE tariff (
    id      UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    type    VARCHAR(50) NOT NULL,
    version BIGINT
);

CREATE TABLE rate (
    id         UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    rate       DOUBLE PRECISION NOT NULL,
    applied_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    tariff_id  UUID             NOT NULL, -- Make sure each rate is associated with a tariff
    CONSTRAINT fk_tariff_rate FOREIGN KEY (tariff_id) REFERENCES tariff (id) ON DELETE CASCADE
);

CREATE TABLE savings_account (
    id                 UUID PRIMARY KEY, -- id matches the Account ID (one-to-one mapping)
    last_calculated_at TIMESTAMP WITHOUT TIME ZONE,
    created_at         TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at         TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    version            BIGINT,
    CONSTRAINT fk_account FOREIGN KEY (id) REFERENCES payment_accounts (id) ON DELETE CASCADE
);

CREATE TABLE tariff_history (
    id                 UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    savings_account_id UUID NOT NULL,
    tariff_id          UUID NOT NULL,
    applied_at         TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT fk_tariff FOREIGN KEY (tariff_id) REFERENCES tariff (id) ON DELETE CASCADE,
    CONSTRAINT fk_savings_account FOREIGN KEY (savings_account_id) REFERENCES savings_account (id) ON DELETE CASCADE
);

ALTER TABLE savings_account
    ADD COLUMN current_tariff_history_id UUID,
    ADD CONSTRAINT fk_current_tariff_history FOREIGN KEY (current_tariff_history_id) REFERENCES tariff_history (id);
