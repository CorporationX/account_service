package faang.school.accountservice.filter.account;

import faang.school.accountservice.dto.filter.AccountFilterDto;
import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.model.Account;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AccountTypeFilterTest {
    private final AccountTypeFilter accountTypeFilter = new AccountTypeFilter();
    private final AccountFilterDto accountFilterDto = new AccountFilterDto(AccountType.INDIVIDUAL_ACCOUNT, null, null);
    private final AccountFilterDto emptyAccountFilterDto = new AccountFilterDto(null, null, null);


    @Test
    public void testIsApplicableNull() {
        assertFalse(accountTypeFilter.isApplicable(emptyAccountFilterDto));
    }

    @Test
    public void testIsApplicableNotNull() {
        assertTrue(accountTypeFilter.isApplicable(accountFilterDto));
    }

    @Test
    public void testApplyFilter() {
        List<Account> accounts = prepareAccounts();
        List<Account> result = accountTypeFilter.apply(accounts.stream(), accountFilterDto).toList();

        assertEquals(1, result.size());
    }

    private List<Account> prepareAccounts() {
        List<Account> accounts = new ArrayList<>();
        Account account = Account.builder()
                .accountType(AccountType.INDIVIDUAL_ACCOUNT)
                .build();
        Account account1 = Account.builder()
                .accountType(AccountType.LEGAL_ENTITY_ACCOUNT)
                .build();
        accounts.add(account);
        accounts.add(account1);
        return accounts;
    }
}
