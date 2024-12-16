CREATE TABLE IF NOT EXISTS public.account
(
    id         BIGSERIAL PRIMARY KEY,
    closed_at  TIMESTAMP(6),
    created_at TIMESTAMP(6),
    currency   VARCHAR(255),
    number     VARCHAR(20) NOT NULL UNIQUE,
    status     VARCHAR(255),
    updated_at TIMESTAMP(6),
    version    VARCHAR(255)
    );

ALTER TABLE public.account
    OWNER TO "user";
