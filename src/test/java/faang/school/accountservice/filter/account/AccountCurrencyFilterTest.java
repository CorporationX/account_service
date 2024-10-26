package faang.school.accountservice.filter.account;

import faang.school.accountservice.dto.filter.AccountFilterDto;
import faang.school.accountservice.enums.Currency;
import faang.school.accountservice.model.Account;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AccountCurrencyFilterTest {
    private final AccountCurrencyFilter accountCurrencyFilter = new AccountCurrencyFilter();
    private final AccountFilterDto accountFilterDto = new AccountFilterDto(null, null, Currency.RUB);
    private final AccountFilterDto emptyAccountFilterDto = new AccountFilterDto(null, null, null);


    @Test
    public void testIsApplicableNull() {
        assertFalse(accountCurrencyFilter.isApplicable(emptyAccountFilterDto));
    }

    @Test
    public void testIsApplicableNotNull() {
        assertTrue(accountCurrencyFilter.isApplicable(accountFilterDto));
    }

    @Test
    public void testApplyFilter() {
        List<Account> accounts = prepareAccounts();
        List<Account> result = accountCurrencyFilter.apply(accounts.stream(), accountFilterDto).toList();

        assertEquals(1, result.size());
    }

    private List<Account> prepareAccounts() {
        List<Account> accounts = new ArrayList<>();
        Account account = Account.builder()
                .currency(Currency.RUB)
                .build();
        Account account1 = Account.builder()
                .currency(Currency.EUR)
                .build();
        accounts.add(account);
        accounts.add(account1);
        return accounts;
    }
}
