CREATE TABLE IF NOT EXISTS public.accounts_owners
(
    id         bigserial PRIMARY KEY,
    owner_id   bigint   NOT NULL,
    owner_type smallint NOT NULL,
    UNIQUE (owner_id, owner_type)
);

CREATE TABLE IF NOT EXISTS public.accounts
(
    id             bigserial PRIMARY KEY,
    account_number varchar(20) NOT NULL UNIQUE CHECK (length(account_number) >= 12),
    owner_id       bigint      NOT NULL,
    account_type   smallint    NOT NULL,
    currency       smallint    NOT NULL,
    status         smallint    NOT NULL DEFAULT 0,
    closed_at      timestamptz,
    version        bigint      NOT NULL DEFAULT 0,
    created_at     timestamptz          DEFAULT current_timestamp,
    updated_at     timestamptz          DEFAULT current_timestamp,
    CONSTRAINT fk_owner_id
        FOREIGN KEY (owner_id) REFERENCES public.accounts_owners (id)
);

insert into accounts_owners(owner_id, owner_type) VALUES (1, 0) ON CONFLICT DO NOTHING;
insert into accounts(account_number, owner_id, account_type, currency, status) VALUES ('123456789012', 1, 0, 0, 0) ON CONFLICT DO NOTHING;
insert into accounts(account_number, owner_id, account_type, currency, status) VALUES ('123456789013', 1, 0, 1, 1) ON CONFLICT DO NOTHING;