package faang.school.accountservice.service;

import faang.school.accountservice.entity.AccountType;
import faang.school.accountservice.entity.FreeAccountNumber;
import faang.school.accountservice.repository.AccountSeqRepository;
import faang.school.accountservice.repository.FreeAccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class FreeAccountNumberServiceIntegrationTest {

    @Autowired
    private FreeAccountNumberService freeAccountNumberService;

    @Autowired
    private FreeAccountRepository freeAccountRepository;

    @Autowired
    private AccountSeqRepository accountSeqRepository;

    @BeforeEach
    void setUp() {
        freeAccountRepository.deleteAll();
        accountSeqRepository.deleteAll();
    }

    @Test
    void testGenerateAccountNumbersDebitSuccessful() {
        freeAccountNumberService.generateAccountNumbers(AccountType.DEBIT, 10);

        var accountNumbers = freeAccountRepository.findAll();
        assertEquals(10, accountNumbers.size());
        /* accountNumbers.forEach(accountNumber -> {
            assertEquals(AccountType.DEBIT, accountNumber.getId().getType());
            assertTrue(accountNumber.getId().getAccountNumber() >= 4200_0000_0000_0000L);
            assertTrue(accountNumber.getId().getAccountNumber() < 4200_0000_0000_0010L);
        }); */
    }

    @Test
    void testGenerateAccountNumbersCreditSuccessful() {
        freeAccountNumberService.generateAccountNumbers(AccountType.CREDIT, 5);

        var accountNumbers = freeAccountRepository.findAll();
        assertEquals(5, accountNumbers.size());
        /*accountNumbers.forEach(accountNumber -> {
            assertEquals(AccountType.CREDIT, accountNumber.getId().getType());
            assertTrue(accountNumber.getId().getAccountNumber() >= 5236_0000_0000_0000L);
            assertTrue(accountNumber.getId().getAccountNumber() < 5236_0000_0000_0005L);
        });*/
    }

    @Test
    void testGenerateAccountNumbersMaxLimitReached() {
        assertThrows(RuntimeException.class, () -> {
            freeAccountNumberService.generateAccountNumbers(AccountType.DEBIT, Integer.MAX_VALUE);
        });
    }

    @Test
    void testRetrieveAccountNumberSuccessful() {
        freeAccountNumberService.generateAccountNumbers(AccountType.DEBIT, 1);

        AtomicReference<FreeAccountNumber> retrievedAccountNumber = new AtomicReference<>();
        freeAccountNumberService.retrieveAccountNumber(AccountType.DEBIT, retrievedAccountNumber::set);

        assertNotNull(retrievedAccountNumber.get());
        //assertEquals(AccountType.DEBIT, retrievedAccountNumber.get().getId().getType());
        //assertEquals(4200_0000_0000_0000L, retrievedAccountNumber.get().getId().getAccountNumber());
    }
}