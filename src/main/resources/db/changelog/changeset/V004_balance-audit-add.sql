CREATE TABLE balance_audit
(
                        id             BIGSERIAL PRIMARY KEY,
                        number         VARCHAR(20) CHECK (length(number) >= 12) UNIQUE NOT NULL,
                        curAuthBalance REAL,
                        curFactBalance REAL,
                        updateOperationId BIGSERIAL,
                        createdAt TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
                        updatedAt TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
                        version   INT NOT NULL DEFAULT 1

)