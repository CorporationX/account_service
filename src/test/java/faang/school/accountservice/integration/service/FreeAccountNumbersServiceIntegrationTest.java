package faang.school.accountservice.integration.service;

import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.integration.IntegrationTestBase;
import faang.school.accountservice.service.FreeAccountNumbersService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FreeAccountNumbersServiceIntegrationTest extends IntegrationTestBase {

    private static final AccountType ACCOUNT_TYPE = AccountType.DEPOSIT;

    @Autowired
    private FreeAccountNumbersService freeAccountNumbersService;


    @Test
    public void checkGetFreeNumber() {
        String expectedResult = "4500000000000001";
        String actualResult = freeAccountNumbersService.getFreeNumber(ACCOUNT_TYPE);
        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void generateUpTo100FreeNumbers() {
        freeAccountNumbersService.generateAccountNumbersUpTo(100, ACCOUNT_TYPE);
    }

}