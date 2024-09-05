package faang.school.accountservice.util;

import faang.school.accountservice.dto.AccountDto;
import faang.school.accountservice.entity.Account;
import faang.school.accountservice.enums.AccountStatus;
import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.enums.Currency;
import lombok.experimental.UtilityClass;

import static java.time.LocalDateTime.now;

@UtilityClass
public final class TestDataFactory {
    public static final String ACCOUNT_NUMBER = "ACC001";
    public static Account createAccount(){
        return Account.builder()
                .id(1L)
                .number("ACC008")
                .type(AccountType.CORPORATEACCOUNT)
                .currency(Currency.EUR)
                .status(AccountStatus.ACTIVE)
                .createdAt(now())
                .updatedAt(now())
                .closedAt(null)
                .version(1)
                .build();
    }
    public static AccountDto createAccountDto(){
        return AccountDto.builder()
                .id(null)
                .number("ACC008")
                .type("CorporateAccount")
                .currency("eur")
                .status("active")
                .createdAt(now())
                .updatedAt(now())
                .closedAt(null)
                .version(1)
                .build();
    }
}
