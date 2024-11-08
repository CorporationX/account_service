package faang.school.accountservice.generator;

import faang.school.accountservice.enums.account.AccountEnum;
import faang.school.accountservice.service.account.FreeAccountNumbersService;
import faang.school.accountservice.util.BaseContextTest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;

import static org.junit.jupiter.api.Assertions.assertEquals;

@EnableScheduling
public class FreeAccountNumbersGeneratorSchedulerIT extends BaseContextTest {

    @Autowired
    private FreeAccountNumbersGeneratorScheduler freeAccountNumbersGeneratorScheduler;

    @Autowired
    private FreeAccountNumbersService freeAccountNumbersService;

    @BeforeAll
    void setUp() {
        freeAccountNumbersService.generateFreeAccountNumber(AccountEnum.DEBIT);
        freeAccountNumbersService.generateFreeAccountNumber(AccountEnum.SAVINGS);
        freeAccountNumbersService.generateAccountNumbers(AccountEnum.DEBIT, 4);
        freeAccountNumbersService.generateAccountNumbers(AccountEnum.SAVINGS, 2);
    }

    @Test
    @DisplayName("Runs every 10 minutes and feels up needs of free account numbers by all types")
    public void whenMethodCalledThenCreatesAccountNumbersNeedsByPropertiesSettingsInSchedule() {
        int debitsBeforeSchedule = freeAccountNumbersService.countAllFreeAccountNumbersByType(AccountEnum.DEBIT);
        int savingsBeforeSchedule = freeAccountNumbersService.countAllFreeAccountNumbersByType(AccountEnum.SAVINGS);

        assertEquals(5, debitsBeforeSchedule);
        assertEquals(3, savingsBeforeSchedule);

        freeAccountNumbersGeneratorScheduler.createRequiredFreeAccountNumbersPool();

        int debits = freeAccountNumbersService.countAllFreeAccountNumbersByType(AccountEnum.DEBIT);
        int savings = freeAccountNumbersService.countAllFreeAccountNumbersByType(AccountEnum.SAVINGS);
        int credit = freeAccountNumbersService.countAllFreeAccountNumbersByType(AccountEnum.CREDIT);
        int deposit = freeAccountNumbersService.countAllFreeAccountNumbersByType(AccountEnum.DEPOSIT);
        int settlement = freeAccountNumbersService.countAllFreeAccountNumbersByType(AccountEnum.SETTLEMENT);
        int subSettlement = freeAccountNumbersService.countAllFreeAccountNumbersByType(AccountEnum.SUBSETTLEMENT);
        int letterOfCredit = freeAccountNumbersService.countAllFreeAccountNumbersByType(AccountEnum.LETTER_OF_CREDIT);
        int budget = freeAccountNumbersService.countAllFreeAccountNumbersByType(AccountEnum.BUDGET);

        assertEquals(10, debits);
        assertEquals(5, savings);
        assertEquals(5, credit);
        assertEquals(5, deposit);
        assertEquals(2, settlement);
        assertEquals(2, subSettlement);
        assertEquals(2, letterOfCredit);
        assertEquals(2, budget);
    }
}
