package faang.school.accountservice.data;

import faang.school.accountservice.dto.AccountDto;
import faang.school.accountservice.entity.Account;
import faang.school.accountservice.enums.AccountStatus;
import faang.school.accountservice.enums.AccountType;
import lombok.Getter;

import static faang.school.accountservice.enums.Currency.USD;

@Getter
public class AccountTestData {
    public static final String ACCOUNT_NUMBER = "12345678901234";
    private final AccountDto accountDto;
    private final Account accountEntity;

    public AccountTestData() {
        accountDto = createAccountDto();
        accountEntity = createAccountEntity();
    }

    private Account createAccountEntity() {
        return Account.builder()
                .id(1L)
                .ownerUserId(1L)
                .number(ACCOUNT_NUMBER)
                .type(AccountType.CREDIT_ACCOUNT)
                .currency(USD)
                .status(AccountStatus.ACTIVE)
                .version(1L)
                .build();
    }

    private AccountDto createAccountDto() {
        return AccountDto.builder()
                .id(1L)
                .ownerUserId(1L)
                .number(ACCOUNT_NUMBER)
                .type(AccountType.CREDIT_ACCOUNT)
                .currency(USD)
                .status(AccountStatus.ACTIVE)
                .build();
    }
}
