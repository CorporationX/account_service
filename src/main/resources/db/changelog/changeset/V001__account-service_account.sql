CREATE TABLE IF NOT EXISTS public.accounts_owners(
    id bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
    owner_id bigint NOT NULL,
    owner_type varchar(128) NOT NULL,
    UNIQUE(owner_id, owner_type)
);

CREATE TABLE IF NOT EXISTS public.accounts(
    id bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
    account_number varchar(20) NOT NULL UNIQUE CHECK(length(account_number) >= 12),
    owner_id bigint NOT NULL,
    account_type varchar(128) NOT NULL,
    currency character(3) NOT NULL,
    status varchar(128) NOT NULL DEFAULT 'ACTIVE',
    closed_at timestamptz,
    version bigint NOT NULL DEFAULT 0,
    created_at timestamptz DEFAULT current_timestamp,
    updated_at timestamptz DEFAULT current_timestamp
);

CREATE INDEX IF NOT EXISTS idx_accounts_owner_id ON public.accounts(owner_id);
CREATE INDEX IF NOT EXISTS idx_accounts_owners_owner_id_owner_type ON public.accounts_owners(owner_id, owner_type);

