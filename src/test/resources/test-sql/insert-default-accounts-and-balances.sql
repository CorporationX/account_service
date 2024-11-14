INSERT INTO cashback_tariff (name, id)
VALUES ('GOOD','9c500958-a278-11ef-b864-0242ac120002'),
       ('GOODSECOND', '65b567b8-a27c-11ef-b864-0242ac120002');

INSERT INTO account (id, number, user_id, type, currency, status, version, cashback_tariff_id)
VALUES ('065977b1-2f8d-47d5-a2a7-c88671a3c5a3', '408124878517', 1, 'DEBIT', 'USD', 'ACTIVE', 0, '9c500958-a278-11ef-b864-0242ac120002'),
       ('f6309d7b-22bd-4b18-a4fa-29a6bdd502e8', '408124878517', 2, 'DEBIT', 'USD', 'ACTIVE', 0, '65b567b8-a27c-11ef-b864-0242ac120002');

INSERT INTO balance (id, account_id, auth_balance, current_balance)
VALUES ('4cc8cd27-9c53-4e4c-8f44-de6a6d7182c0', '065977b1-2f8d-47d5-a2a7-c88671a3c5a3', 0, 1000),
       ('bd4a870b-8ffa-4919-a1a4-57c0cb1138a3', 'f6309d7b-22bd-4b18-a4fa-29a6bdd502e8', 0, 0);

-- INSERT INTO auth_payment (id, source_balance_id, target_balance_id, amount, status, category)
-- VALUES ('8455ca84-1f65-4f16-b815-0933fe5c1631', )