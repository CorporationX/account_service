ALTER TABLE public.account
    ADD COLUMN IF NOT EXISTS tariff_id BIGINT NOT NULL,
    ADD COLUMN IF NOT EXISTS cashback_balance NUMERIC(19, 2) DEFAULT 0 NOT NULL;

CREATE INDEX IF NOT EXISTS idx_account_tariff_id ON public.account (tariff_id);