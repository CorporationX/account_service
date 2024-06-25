CREATE TABLE account
(
    id             BIGSERIAL    PRIMARY KEY UNIQUE,
    owner_id       BIGINT       NOT NULL,
    owner_type     VARCHAR(7)   NOT NULL,
    number         VARCHAR(20)  NOT NULL UNIQUE CHECK (LENGTH(number) >= 12 AND number SIMILAR TO '[0-9]+'),
    type           VARCHAR(4)   NOT NULL,
    currency       VARCHAR(3)   NOT NULL,
    status         VARCHAR(8)   NOT NULL DEFAULT 'ACTIVE',
    status_details VARCHAR(128),
    created_at     TIMESTAMPTZ  DEFAULT CURRENT_TIMESTAMP,
    updated_at     TIMESTAMPTZ  DEFAULT CURRENT_TIMESTAMP,
    closed_at      TIMESTAMPTZ,
    version        BIGINT       NOT NULL DEFAULT 0
);

CREATE TABLE balance_audit
(
    id                      BIGSERIAL PRIMARY KEY UNIQUE,
    account_id              BIGSERIAL    NOT NULL,
    balance_audit_version   BIGINT NOT NULL,
    auth_balance            BIGINT,
    actual_balance          BIGINT,
    operation_id            BIGINT,
    audit_timestamp         timestamptz default current_timestamp,
    CONSTRAINT fk_account_id FOREIGN KEY (account_id) REFERENCES account (id)
);

CREATE INDEX index_number_payment ON account (number);
CREATE INDEX index_owner_id ON account (owner_id);

CREATE TABLE balance
(
    id             BIGSERIAL  PRIMARY KEY UNIQUE,
    account_id     BIGINT     NOT NULL,
    auth_balance   BIGINT,
    actual_balance BIGINT,
    created_at     TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    last_update_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    version        BIGINT      NOT NULL,
    currency       VARCHAR(4)  NOT NULL,

    CONSTRAINT fk_account_id FOREIGN KEY (account_id) REFERENCES account (id)
);