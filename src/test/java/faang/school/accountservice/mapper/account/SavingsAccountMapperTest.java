package faang.school.accountservice.mapper.account;

import faang.school.accountservice.dto.account.SavingsAccountDto;
import faang.school.accountservice.entity.account.Account;
import faang.school.accountservice.entity.account.SavingsAccount;
import faang.school.accountservice.entity.rate.Rate;
import faang.school.accountservice.entity.tariff.Tariff;
import faang.school.accountservice.mapper.rate.RateMapperImpl;
import faang.school.accountservice.mapper.tariff.TariffMapperImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.*;

class SavingsAccountMapperTest {

    private final SavingsAccountMapperImpl savingsAccountMapper =
            new SavingsAccountMapperImpl(new TariffMapperImpl(new RateMapperImpl()));

    private static final Long ID = 1L;
    private static final String NUMBER = "number";
    private static final String TARIFF_TYPE = "type";
    private static final Double INTEREST_RATE = 5.0;
    private static final String RATE_HISTORY = "{5.0}";
    private SavingsAccount savingsAccount;

    @BeforeEach
    public void init() {
        savingsAccount = SavingsAccount.builder()
                .id(ID)
                .number(NUMBER)
                .account(Account.builder()
                        .id(ID)
                        .build())
                .tariff(Tariff.builder()
                        .id(ID)
                        .tariffType(TARIFF_TYPE)
                        .rate(Rate.builder()
                                .id(ID)
                                .interestRate(INTEREST_RATE)
                                .build())
                        .rateHistory(RATE_HISTORY)
                        .build())
                .build();
    }


    @Test
    @DisplayName("Успех при маппинге SavingsAccount в SavingsAccountDto")
    public void testToDto() {
        SavingsAccountDto resultSavingsAccountDto = savingsAccountMapper.toDto(savingsAccount);

        assertNotNull(resultSavingsAccountDto);
        assertEquals(savingsAccount.getId(), resultSavingsAccountDto.getId());
        assertEquals(savingsAccount.getNumber(), resultSavingsAccountDto.getNumber());
        assertEquals(savingsAccount.getAccount().getId(), resultSavingsAccountDto.getAccountId());

        assertNotNull(resultSavingsAccountDto.getTariffDto());
        assertEquals(savingsAccount.getTariff().getId(),
                resultSavingsAccountDto.getTariffDto().getId());
        assertEquals(savingsAccount.getTariff().getTariffType(),
                resultSavingsAccountDto.getTariffDto().getTariffType());
        assertEquals(savingsAccount.getTariff().getRate().getId(),
                resultSavingsAccountDto.getTariffDto().getRateDto().getId());
        assertEquals(savingsAccount.getTariff().getRate().getInterestRate(),
                resultSavingsAccountDto.getTariffDto().getRateDto().getInterestRate());
        assertEquals(savingsAccount.getTariff().getRateHistory(),
                resultSavingsAccountDto.getTariffDto().getRateHistory());
    }
}