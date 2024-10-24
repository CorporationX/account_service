CREATE TABLE balance_audit
(
                        id                      BIGSERIAL PRIMARY KEY,
                        balance_id              BIGINT NOT NULL,
                        number                  VARCHAR(20) CHECK (length(number) >= 12) UNIQUE NOT NULL,
                        curAuthBalance          REAL,
                        curFactBalance          REAL,
                        updateOperationId       BIGSERIAL,
                        createdAt               TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
                        updatedAt               TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
                        version                 INTEGER NOT NULL DEFAULT 1,

                        FOREIGN KEY (balance_id) REFERENCES balance(id)

)