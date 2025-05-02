CREATE SEQUENCE balance_balance_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER TABLE balance
    ALTER COLUMN balance_id SET DEFAULT nextval('balance_balance_id_seq');
