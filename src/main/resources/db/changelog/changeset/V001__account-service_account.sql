-- Write your sql migration here!
CREATE TABLE account (
    id bigserial primary key,
    number varchar(20) CHECK (LENGTH(number) BETWEEN 12 AND 20) NOT NULL,
    project_account boolean NOT NULL DEFAULT FALSE,
    owner_id bigint NOT NULL,
    type smallint NOT NULL,
    currency smallint NOT NULL,
    status smallint NOT NULL DEFAULT 0,
    created_at timestamptz DEFAULT current_timestamp,
    updated_at timestamptz DEFAULT current_timestamp,
    closed_at timestamptz,
    version bigint NOT NULL DEFAULT 0
);

create index number_idx on account (number);
create index owner_project_idx on account (owner_id, project_account);