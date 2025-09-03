CREATE TABLE IF NOT EXISTS savings_account
(
    account_id          UUID PRIMARY KEY UNIQUE,
    balance             NUMERIC(18, 2) DEFAULT 0 NOT NULL,
    last_interest_at    timestamptz,
    created_at          timestamptz DEFAULT current_timestamp,
    updated_at          timestamptz DEFAULT current_timestamp,
    version             int DEFAULT 0 NOT NULL,

    CONSTRAINT fk_account_id FOREIGN KEY (account_id) REFERENCES account (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS tariff
(
    id                  bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
    type                VARCHAR(32) NOT NULL UNIQUE,
    created_at          timestamptz DEFAULT current_timestamp,
    updated_at          timestamptz DEFAULT current_timestamp
);

CREATE INDEX IF NOT EXISTS idx_tariff_type ON tariff(type);

CREATE TABLE IF NOT EXISTS tariff_history
(
    id                  bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
    account_id          UUID NOT NULL,
    tariff_id           bigint NOT NULL,
    created_at          timestamptz DEFAULT current_timestamp,

    CONSTRAINT fk_account_id FOREIGN KEY (account_id) REFERENCES account (id) ON DELETE CASCADE,
    CONSTRAINT fk_tariff_id FOREIGN KEY (tariff_id) REFERENCES tariff (id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_account_id ON tariff_history(account_id);
CREATE INDEX IF NOT EXISTS idx_tariff_id ON tariff_history(tariff_id);

CREATE TABLE IF NOT EXISTS tariff_rate_history
(
    id                  bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
    tariff_id           bigint NOT NULL,
    rate                NUMERIC(5, 2) NOT NULL,
    created_at          timestamptz DEFAULT current_timestamp,

    CONSTRAINT fk_tariff_id FOREIGN KEY (tariff_id) REFERENCES tariff (id) ON DELETE CASCADE
);