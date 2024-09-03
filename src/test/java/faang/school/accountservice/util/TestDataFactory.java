package faang.school.accountservice.util;

import faang.school.accountservice.dto.AccountDto;
import faang.school.accountservice.dto.TariffAndRateDto;
import faang.school.accountservice.entity.Account;
import faang.school.accountservice.entity.RateHistory;
import faang.school.accountservice.entity.SavingsAccount;
import faang.school.accountservice.entity.Tariff;
import faang.school.accountservice.enums.AccountStatus;
import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.enums.Currency;
import faang.school.accountservice.enums.TariffType;
import lombok.experimental.UtilityClass;

import java.time.LocalDateTime;
import java.util.Stack;

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

    public static SavingsAccount createSavingsAccount(){
        var account = createAccount();

        var tariff = createTariff();
        Stack<Tariff> tariffStack = new Stack<>();
        tariffStack.push(tariff);

        return  SavingsAccount.builder()
                .id(1L)
                .account(account)
                .tariffHistory(tariffStack)
                .lastInterestCalculationDate(LocalDateTime.now().minusMonths(1))
                .createdAt(LocalDateTime.now().minusYears(1))
                .updatedAt(LocalDateTime.now())
                .version(1)
                .build();
    }
    public static Tariff createTariff(){
        var rateHistory = createRateHistory();
        Stack<RateHistory> rateHistoryStack = new Stack<>();
        rateHistoryStack.push(rateHistory);

        return Tariff.builder()
                .id(1L)
                .type(TariffType.PREMIUM)
                .savingsAccount(null)
                .appliedAt(LocalDateTime.now().minusMonths(6))
                .rateHistoryList(rateHistoryStack)
                .build();
    }

    public static RateHistory createRateHistory(){
        return RateHistory.builder()
                .id(1L)
                .rate(0.08)
                .createdAt(LocalDateTime.now().minusMonths(6))
                .build();
    }

    public static TariffAndRateDto createTariffAndRateDto(){
        return TariffAndRateDto.builder()
                .tariffType("PREMIUM")
                .rate(0.08)
                .build();
    }
}
