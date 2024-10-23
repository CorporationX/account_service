CREATE SEQUENCE IF NOT EXISTS accounts_id_seq START WITH 1 INCREMENT BY 1 MAXVALUE 9223372036854775807;
CREATE SEQUENCE IF NOT EXISTS accounts_owners_id_seq START WITH 1 INCREMENT BY 1 MAXVALUE 9223372036854775807;

CREATE TABLE IF NOT EXISTS public.accounts_owners(
    id bigint PRIMARY KEY DEFAULT nextval('accounts_owners_id_seq'),
    owner_id bigint NOT NULL,
    owner_type smallint NOT NULL,
    UNIQUE(owner_id, owner_type)
);

CREATE TABLE IF NOT EXISTS public.accounts(
    id bigint PRIMARY KEY DEFAULT nextval('accounts_id_seq'),
    account_number varchar(20) NOT NULL UNIQUE CHECK(length(account_number) >= 12),
    owner_id bigint NOT NULL,
    account_type smallint NOT NULL,
    currency smallint NOT NULL,
    status smallint NOT NULL DEFAULT 0,
    closed_at timestamptz,
    version bigint NOT NULL DEFAULT 0,
    created_at timestamptz DEFAULT current_timestamp,
    updated_at timestamptz DEFAULT current_timestamp,
    CONSTRAINT fk_owner_id
        FOREIGN KEY(owner_id) REFERENCES public.accounts_owners(id)
);

CREATE INDEX IF NOT EXISTS idx_accounts_owner_id ON public.accounts(owner_id);

