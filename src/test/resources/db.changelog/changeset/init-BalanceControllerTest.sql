CREATE TABLE users(
                      id BIGINT UNIQUE
);

INSERT INTO users(id)
VALUES (1);

CREATE TABLE account
(
    id                BIGINT UNIQUE,
    number            VARCHAR(20) CHECK (length(number) >= 12) UNIQUE NOT NULL,
    holder_user_id    BIGINT,
    holder_project_id BIGINT,
    type              SMALLINT NOT NULL,
    currency          SMALLINT NOT NULL,
    status            SMALLINT NOT NULL        DEFAULT 0,
    version           SMALLINT NOT NULL        DEFAULT 1,
    created_at        TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at        TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    closed_at         TIMESTAMP WITH TIME ZONE,
    balance_id BIGINT,

    CONSTRAINT chk_one_owner CHECK (
        (holder_user_id IS NOT NULL AND holder_project_id IS NULL) OR
        (holder_user_id IS NULL AND holder_project_id IS NOT NULL)
        )
);

CREATE INDEX ON account (holder_user_id);
CREATE INDEX ON account (holder_project_id);

INSERT INTO account(id, NUMBER, HOLDER_USER_ID, TYPE, CURRENCY, CLOSED_AT)
VALUES (1, '1111111111111', 1, 1, 1, now());

CREATE TABLE  balance(
                         id bigint PRIMARY KEY,
                         account_id BIGSERIAL,
                         cur_auth_balance REAL,
                         cur_fact_balance REAL,
                         created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
                         updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
                         CONSTRAINT bank_account
                             FOREIGN KEY(account_id) REFERENCES account(id),
                         version INT NOT NULL DEFAULT 1
);

INSERT INTO balance(ID, CUR_AUTH_BALANCE, CUR_FACT_BALANCE)
VALUES (1, 100, 100);