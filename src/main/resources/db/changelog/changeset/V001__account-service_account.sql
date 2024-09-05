-- Write your sql migration here!
CREATE TABLE account (
    id bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
    number varchar(20) UNIQUE NOT NULL,
    owner_id bigint NOT NULL,
    owner_type varchar(16) NOT NULL,
    account_type varchar(64) NOT NULL,
    currency varchar(16) NOT NULL,
    status varchar(16) NOT NULL,
    created_at timestamptz DEFAULT current_timestamp,
    updated_at timestamptz DEFAULT current_timestamp,
    closed_at timestamptz,
    version int NOT NULL
);

CREATE UNIQUE INDEX account_number_index
    ON public.account USING btree (number);
CREATE UNIQUE INDEX account_owner_id_owner_type_index
    ON public.account USING btree (owner_id, owner_type);

