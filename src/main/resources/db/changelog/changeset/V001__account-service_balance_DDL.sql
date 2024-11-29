create table balance (
   id bigint primary key GENERATED ALWAYS AS IDENTITY UNIQUE,
   authorization_balance DECIMAL,
   actual_balance DECIMAL,
   created_at timestamptz DEFAULT current_timestamp,
   updated_at timestamptz DEFAULT current_timestamp,
   version INTEGER,
   account_id INT NOT NULL,

   CONSTRAINT fk_account_id FOREIGN KEY (account_id) REFERENCES account (id)
);
