insert into public.accounts_owners(owner_id,  owner_type)
values (1, 0), (2, 0);

insert into public.accounts(
    account_number,  owner_id,   account_type,  currency,
    status,          closed_at,  version
)
values (1111111111111111, (select lead(id, 1) over(order by id desc) from public.accounts_owners fetch first 1 rows only)
        , 0, 0, 0, null, 1),
    (2222222222222222, (select max(id) from public.accounts_owners), 0, 0, 0, null, 1);

insert into public.balance (account_id,  auth_balance,  actual_balance, version)
values((select lead(id, 1) over(order by id desc) from public.accounts fetch first 1 rows only), 1000, 1000, 1),
    ((select max(id) from public.accounts), 500, 500, 1);