CREATE TABLE merchants
(
    id            SERIAL PRIMARY KEY,
    merchant_id   BIGINT      NOT NULL,
    merchant_type VARCHAR(16) NOT NULL,
    created_at    timestamptz DEFAULT CURRENT_TIMESTAMP,
    updated_at    timestamptz DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE cashback_plans
(
    id          SERIAL PRIMARY KEY,
    description VARCHAR(256),
    created_at  timestamptz DEFAULT CURRENT_TIMESTAMP,
    updated_at  timestamptz DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE cashback_rules
(
    id               SERIAL PRIMARY KEY,
    transaction_type VARCHAR(32),
    merchant_id      BIGINT,
    percentage       SMALLINT NOT NULL,
    created_at       timestamptz DEFAULT CURRENT_TIMESTAMP,
    updated_at       timestamptz DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fK_merchant_id FOREIGN KEY (merchant_id) REFERENCES merchants(id)
);

CREATE TABLE cashback_plans_rules
(
    cashback_plan_id BIGINT NOT NULL,
    cashback_rule_id BIGINT NOT NULL,

    PRIMARY KEY (cashback_plan_id, cashback_rule_id),
    CONSTRAINT fk_cashback_plan_id FOREIGN KEY (cashback_plan_id) REFERENCES cashback_plans(id),
    CONSTRAINT fk_cashback_rule_id FOREIGN KEY (cashback_rule_id) REFERENCES cashback_rules(id)
);

INSERT INTO merchants (merchant_id, merchant_type)
VALUES
    (1, 'USER'),
    (2, 'PROJECT');

CREATE UNIQUE INDEX idx_transaction_type_merchant_id_percentage
    ON cashback_rules (merchant_id, percentage, transaction_type);

CREATE INDEX idx_cashback_rule_id
    ON cashback_plans_rules (cashback_rule_id);

ALTER TABLE accounts
    ADD COLUMN cashback_plan_id BIGINT,
    ADD CONSTRAINT fk_cashback_plan_id
        FOREIGN KEY (cashback_plan_id) REFERENCES cashback_plans (id);