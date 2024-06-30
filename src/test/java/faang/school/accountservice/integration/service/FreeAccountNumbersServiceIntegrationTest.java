package faang.school.accountservice.integration.service;

import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.integration.IntegrationTestBase;
import faang.school.accountservice.service.FreeAccountNumbersService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FreeAccountNumbersServiceIntegrationTest extends IntegrationTestBase {

    @Autowired
    private FreeAccountNumbersService freeAccountNumbersService;


    @Test
    public void checkGetFreeNumber() {
        String expectedResult = "4500000000000001";
        String actualResult = freeAccountNumbersService.getFreeNumber(AccountType.DEPOSIT);
        assertEquals(expectedResult, actualResult);
    }

}