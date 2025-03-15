CREATE SEQUENCE account_seq
START WITH 1
INCREMENT BY 1
MINVALUE 1
CYCLE
CACHE 50;

CREATE TABLE account(id bigint PRIMARY KEY DEFAULT nextval('account_seq'),
                     account_number VARCHAR(20) NOT NULL,
                     balance DECIMAL(15, 2) DEFAULT 0.00 NOT NULL,
                     author_id bigint,
                     project_id bigint,
                     account_type VARCHAR(50) NOT NULL,
                     currency VARCHAR(10) NOT NULL,
                     status VARCHAR(20) DEFAULT '0' NOT NULL,
                     created_at timestamptz DEFAULT current_timestamp,
                     updated_at timestamptz DEFAULT current_timestamp,
                     closed_at timestamptz DEFAULT NULL,
                     version int DEFAULT 1 NOT NULL

                     CONSTRAINT account_author_or_project_not_null CHECK (author_id IS NOT NULL OR project_id IS NOT NULL),
                     CONSTRAINT account_number_length CHECK (LENGTH(account_number) BETWEEN 12 AND 20),
                     CONSTRAINT account_number_unique UNIQUE (account_number)
                     );

CREATE INDEX idx_author_id ON account(author_id);
CREATE INDEX idx_project_id ON account(project_id);