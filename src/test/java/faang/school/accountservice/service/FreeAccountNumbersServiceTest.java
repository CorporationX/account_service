package faang.school.accountservice.service;

import faang.school.accountservice.entity.AccountNumbersSequence;
import faang.school.accountservice.repository.AccountNumbersSequenceRepository;
import faang.school.accountservice.services.FreeAccountNumbersService;
import faang.school.accountservice.util.BaseContextTest;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;


@Transactional
public class FreeAccountNumbersServiceTest extends BaseContextTest {

    @Autowired
    private FreeAccountNumbersService freeAccountNumbersService;

    @Autowired
    private AccountNumbersSequenceRepository accountNumbersSequenceRepository;

    @Test
    void testGetFreeAccountNumberSuccessTest() {
        String accountType = "SAVINGS";
        Long existingAccountNumber = 523600000000000L;
        freeAccountNumbersService.createNewAccountNumbersSequence(accountType);
        Consumer<Long> action = mock(Consumer.class);
        Long result = freeAccountNumbersService.getFreeAccountNumberWithTransaction(accountType, action);
        assertEquals(existingAccountNumber, result);
    }


    @Test
    void createNewFreeAccountNumberWrongAccountTypeFailedTest() {
        String accountType = "INVALID";

        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                freeAccountNumbersService.createNewFreeAccountNumber(accountType));
        assertEquals("Invalid account type: " + accountType, exception.getMessage());
    }

    @Test
    void createNewAccountNumbersSequenceSuccessTest() {
        String accountType = "CHECKING";
        freeAccountNumbersService.createNewAccountNumbersSequence(accountType);
        AccountNumbersSequence sequence = accountNumbersSequenceRepository.findByAccountType(accountType);
        assertNotNull(sequence);
        assertEquals("CHECKING", sequence.getAccountType());
        assertEquals(0L, sequence.getCurrent());
    }

    @Test
    void createNewAccountNumbersSequenceFailedTest() {
        String accountType = "CHECKING";

        accountNumbersSequenceRepository.createAccountTypeCounter(accountType);
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                freeAccountNumbersService.createNewAccountNumbersSequence(accountType));

        assertEquals("AccountNumbersSequence for account type " + accountType + " is exist", exception.getMessage());
    }

    @Test
    void getFreeAccountNumberWithTransactionSuccessTest() {

        String accountType = "SAVINGS";
        Consumer<Long> action = mock(Consumer.class);
        freeAccountNumbersService.createNewAccountNumbersSequence(accountType);
        freeAccountNumbersService.getFreeAccountNumberWithTransaction(accountType, action);
        AccountNumbersSequence sequence = accountNumbersSequenceRepository.findByAccountType(accountType);
        assertEquals(1L, sequence.getCurrent());
    }
}