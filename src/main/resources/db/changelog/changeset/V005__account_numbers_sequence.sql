CREATE TABLE public.account_numbers_sequence (
    accounttype VARCHAR(32) NOT NULL PRIMARY KEY,
    counter BIGINT NOT NULL DEFAULT 1
);
