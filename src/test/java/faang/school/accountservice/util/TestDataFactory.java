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
import java.util.ArrayList;
import java.util.List;

import static java.time.LocalDateTime.now;
import static java.util.List.of;

@UtilityClass
public final class TestDataFactory {
    public static final String ACCOUNT_NUMBER = "8800 0000 0000 0008";
    public static Account createAccount(){
        return Account.builder()
                .id(1L)
                .number("8800 0000 0000 0008")
                .type(AccountType.SAVINGSACCOUNT)
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
                .number("8800 0000 0000 0008")
                .type("SAVINGSACCOUNT")
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

        return  SavingsAccount.builder()
                .id(1L)
                .account(account)
                .tariffHistory(of(tariff))
                .lastInterestCalculationDate(LocalDateTime.now().minusMonths(1))
                .createdAt(LocalDateTime.now().minusYears(1))
                .updatedAt(LocalDateTime.now())
                .version(1)
                .build();
    }
    public static Tariff createTariff(){
        var rateHistory = createRateHistory();

        return Tariff.builder()
                .id(1L)
                .type(TariffType.PREMIUM)
                .appliedAt(LocalDateTime.now().minusMonths(6))
                .rateHistoryList(new ArrayList<>(of(rateHistory)))
                .build();
    }

    public static RateHistory createRateHistory(){
        return RateHistory.builder()
                .id(1L)
                .rate(0.08)
                .createdAt(LocalDateTime.now())
                .build();
    }

    public static TariffAndRateDto createTariffAndRateDto(){
        return TariffAndRateDto.builder()
                .tariffType("PREMIUM")
                .rate(0.08)
                .build();
    }

    public static List<RateHistory> createRateHistoryList(){
        var rateHistoryFirst = createRateHistory();
        var rateHistorySecond = createRateHistory();
        rateHistorySecond.setId(2L);
        rateHistorySecond.setRate(0.02);
        var rateHistoryThird = createRateHistory();
        rateHistoryThird.setId(3L);
        rateHistoryThird.setRate(0.03);
        return List.of(rateHistoryFirst, rateHistorySecond, rateHistoryThird);
    }
    public static AccountDto createAccountDtoForSaving(){
        return AccountDto.builder()
                .number(null)
                .type("SAVINGSACCOUNT")
                .currency("eur")
                .status("active")
                .createdAt(now())
                .updatedAt(now())
                .closedAt(null)
                .version(3)
                .build();
    }
}
