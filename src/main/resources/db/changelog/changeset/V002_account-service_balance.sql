CREATE TABLE balance (
      id                  UUID             PRIMARY KEY DEFAULT gen_random_uuid(),
      account_id          UUID             NOT NULL UNIQUE
                                          REFERENCES account(id) ON DELETE RESTRICT,
      authorized_balance  NUMERIC(19,4)    NOT NULL,
      actual_balance      NUMERIC(19,4)    NOT NULL,
      created_at          TIMESTAMPTZ      NOT NULL,
      updated_at          TIMESTAMPTZ      NOT NULL,
      version             BIGINT           NOT NULL DEFAULT 0
);