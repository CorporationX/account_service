CREATE TABLE account
(
    id            bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
    number        bigint UNIQUE NOT NULL,
    project_id    bigint UNIQUE,
    user_id       bigint UNIQUE,
    type          varchar(64)   NOT NULL,
    status        varchar(64)   NOT NULL,
    created_at    timestamptz DEFAULT current_timestamp,
    updated_at    timestamptz DEFAULT current_timestamp,
    closed_at     timestamptz DEFAULT current_timestamp,
--     version       bigint,

    CONSTRAINT fk_project_id FOREIGN KEY (project_id) REFERENCES project (id),
    CONSTRAINT fk_user_id FOREIGN KEY (user_id) REFERENCES user (id)
);