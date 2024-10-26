package faang.school.accountservice.filter.account;

import faang.school.accountservice.dto.filter.AccountFilterDto;
import faang.school.accountservice.enums.AccountStatus;
import faang.school.accountservice.model.Account;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AccountStatusFilterTest {
    private final AccountStatusFilter accountStatusFilter = new AccountStatusFilter();
    private final AccountFilterDto accountFilterDto = new AccountFilterDto(null, AccountStatus.ACTIVE, null);
    private final AccountFilterDto emptyAccountFilterDto = new AccountFilterDto(null, null, null);


    @Test
    public void testIsApplicableNull() {
        assertFalse(accountStatusFilter.isApplicable(emptyAccountFilterDto));
    }

    @Test
    public void testIsApplicableNotNull() {
        assertTrue(accountStatusFilter.isApplicable(accountFilterDto));
    }

    @Test
    public void testApplyFilter() {
        List<Account> accounts = prepareAccounts();
        List<Account> result = accountStatusFilter.apply(accounts.stream(), accountFilterDto).toList();

        assertEquals(1, result.size());
    }

    private List<Account> prepareAccounts() {
        List<Account> accounts = new ArrayList<>();
        Account account = Account.builder()
                .status(AccountStatus.ACTIVE)
                .build();
        Account account1 = Account.builder()
                .status(AccountStatus.FROZEN)
                .build();
        accounts.add(account);
        accounts.add(account1);
        return accounts;
    }
}
