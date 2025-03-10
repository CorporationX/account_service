package faang.school.accountservice.service;

import faang.school.accountservice.entity.AccountSeq;
import faang.school.accountservice.entity.AccountType;
import faang.school.accountservice.entity.FreeAccountNumber;
import faang.school.accountservice.repository.AccountSeqRepository;
import faang.school.accountservice.repository.FreeAccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import static faang.school.accountservice.entity.AccountType.CREDIT;
import static faang.school.accountservice.entity.AccountType.DEBIT;
import static faang.school.accountservice.entity.AccountType.FOR_TEST;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class FreeAccountNumberServiceIntegrationTest {

    @Autowired
    private FreeAccountNumberService freeAccountNumberService;

    @Autowired
    private FreeAccountRepository freeAccountRepository;

    @Autowired
    private AccountSeqRepository accountSeqRepository;

    private static final long DEBIT_ACCOUNT_PATTERN = 4200_0000_0000_0000L;
    private static final long CREDIT_ACCOUNT_PATTERN = 5236_0000_0000_0000L;

    @BeforeEach
    void setUp() {
        freeAccountRepository.deleteAll();
        accountSeqRepository.deleteAll();

        freeAccountNumberService.addAccountType(DEBIT, DEBIT_ACCOUNT_PATTERN);
        freeAccountNumberService.addAccountType(CREDIT, CREDIT_ACCOUNT_PATTERN);

        AccountSeq initialSeq = new AccountSeq();
        initialSeq.setType(DEBIT);
        initialSeq.setCounter(0);
        accountSeqRepository.save(initialSeq);
    }

    @Test
    void testGenerateDebitAccountNumbersSuccessful() {
        freeAccountNumberService.generateAccountNumbers(DEBIT, 10);
        var accountNumbers = freeAccountRepository.findAll();
        assertEquals(10, accountNumbers.size());
        accountNumbers.forEach(accountNumber -> {
            assertEquals(AccountType.DEBIT, accountNumber.getId().getType());
            assertTrue(accountNumber.getId().getAccountNumber() >= 4200_0000_0000_0000L);
            assertTrue(accountNumber.getId().getAccountNumber() < 4200_0000_0000_0010L);
        });
    }

    @Test
    void testGenerateCreditAccountNumbersSuccessful() {
        freeAccountNumberService.generateAccountNumbers(AccountType.CREDIT, 5);

        var accountNumbers = freeAccountRepository.findAll();
        assertEquals(5, accountNumbers.size());
        accountNumbers.forEach(accountNumber -> {
            assertEquals(AccountType.CREDIT, accountNumber.getId().getType());
            assertTrue(accountNumber.getId().getAccountNumber() >= 5236_0000_0000_0000L);
            assertTrue(accountNumber.getId().getAccountNumber() < 5236_0000_0000_0005L);
        });
    }

    @Test
    void testRetrieveDebitAccountNumberSuccessful() {
        freeAccountNumberService.generateAccountNumbers(DEBIT, 5);
        AtomicReference<FreeAccountNumber> result = new AtomicReference<>();
        Consumer<FreeAccountNumber> numberConsumer = result::set;

        freeAccountNumberService.retrieveAccountNumber(DEBIT, numberConsumer);

        FreeAccountNumber retrievedAccountNumber = result.get();
        assertNotNull(retrievedAccountNumber, "Retrieved account number should not be null");
        assertEquals(DEBIT, retrievedAccountNumber.getId().getType());
        assertEquals(4200_0000_0000_0000L, retrievedAccountNumber.getId().getAccountNumber(),
                "Retrieved account number should match the test data");
    }

    @Test
    void testRetrieveCreditAccountNumberSuccessful() {
        freeAccountNumberService.generateAccountNumbers(CREDIT, 5);
        AtomicReference<FreeAccountNumber> result = new AtomicReference<>();
        Consumer<FreeAccountNumber> numberConsumer = result::set;

        freeAccountNumberService.retrieveAccountNumber(CREDIT, numberConsumer);

        FreeAccountNumber retrievedAccountNumber = result.get();
        assertNotNull(retrievedAccountNumber, "Retrieved account number should not be null");
        assertEquals(CREDIT, retrievedAccountNumber.getId().getType());
        assertEquals(5236_0000_0000_0000L, retrievedAccountNumber.getId().getAccountNumber(),
                "Retrieved account number should match the test data");
    }

    @Test
    void testGenerateAccountNumbersForUnknownTypeException() {
        Exception exception = assertThrows(RuntimeException.class, () -> {
            freeAccountNumberService.generateAccountNumbers(AccountType.valueOf("UNKNOWN"), 10);
        });

        String expectedMessage = "No enum constant faang.school.accountservice.entity.AccountType.UNKNOWN";
        String actualMessage = exception.getMessage();
        assertEquals(actualMessage, expectedMessage);
    }

    @Test
    void testGenerateAccountNumbers_MaxAccountNumberReached() {
        AccountSeq accountSeq = new AccountSeq();
        accountSeq.setType(DEBIT);
        accountSeq.setCounter(1_0000_0000_0000L);
        accountSeqRepository.save(accountSeq);

        Exception exception = assertThrows(RuntimeException.class, () -> {
            freeAccountNumberService.generateAccountNumbers(DEBIT, 10);
        });

        String expectedMessage = "The maximum account number has been reached";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void testGenerateAccountNumbersForNotAddedTypeException() {
        Exception exception = assertThrows(RuntimeException.class, () -> {
            freeAccountNumberService.generateAccountNumbers(FOR_TEST, 10);
        });

        String expectedMessage = "Unknown type FOR_TEST";
        String actualMessage = exception.getMessage();
        assertEquals(actualMessage, expectedMessage);
    }
}