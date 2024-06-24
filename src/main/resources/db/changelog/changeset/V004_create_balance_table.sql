ALTER TABLE balance
    ADD COLUMN authorized_balance numeric(30, 2) not null default 0,
    ADD COLUMN created_at TIMESTAMP NOT NULL DEFAULT NOW();