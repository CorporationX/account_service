package faang.school.accountservice.service;

import faang.school.accountservice.entity.FreeAccountNumber;
import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.repository.FreeAccountNumbersRepository;
import faang.school.accountservice.util.BaseContextTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class FreeAccountNumberServiceIT extends BaseContextTest {

    @Autowired
    FreeAccountNumbersService freeAccountNumbersService;

    @Autowired
    FreeAccountNumbersRepository accountNumbersRepository;

    @Test
    public void getFreeAccountNumberTest() {
        AccountType accountType = AccountType.DEBIT;

        String resultNumber = freeAccountNumbersService.getFreeAccountNumber(accountType);
        assertNotNull(resultNumber);
        assertEquals("6555000000000001", resultNumber);
    }

    @Test
    public void generateFreeAccountNumberTest() {
        AccountType accountType = AccountType.SAVINGS;

        freeAccountNumbersService.generateFreeAccountNumber(accountType);
        FreeAccountNumber result = accountNumbersRepository.getFirstByAccountType(accountType);

        assertNotNull(result);
        assertEquals("5444000000000001", result.getAccountNumber());
    }
}