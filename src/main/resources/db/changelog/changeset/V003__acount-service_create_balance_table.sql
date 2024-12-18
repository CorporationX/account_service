CREATE TABLE public.balance(
    id BIGSERIAL PRIMARY KEY,
    account_id BIGINT NOT NULL,
    auth_balance DECIMAL(18, 2) DEFAULT 0 NOT NULL,
    actual_balance DECIMAL(18, 2) DEFAULT 0 NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    version INT DEFAULT 1 NOT NULL,
    CONSTRAINT fk_account FOREIGN KEY (account_id) REFERENCES public.account (id)
);