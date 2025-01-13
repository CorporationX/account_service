package faang.school.accountservice.repository;

import faang.school.accountservice.entity.AccountNumbersSequence;
import faang.school.accountservice.util.BaseContextTest;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
public class AccountNumbersSequenceRepositoryTest extends BaseContextTest {

    @Autowired
    private AccountNumbersSequenceRepository accountNumbersSequenceRepository;

    @Test
    void createNewAccountNumbersSequenceSuccessTest() {
        String accountType = "CHECKING";
        accountNumbersSequenceRepository.createAccountTypeCounter(accountType);
        AccountNumbersSequence sequence = accountNumbersSequenceRepository.findByAccountType(accountType);
        assertEquals(accountType, sequence.getAccountType());
    }

    @Test
    void createAccountTypeCounterSuccessTest() {
        accountNumbersSequenceRepository.createAccountTypeCounter("SAVINGS");

        AccountNumbersSequence sequence = accountNumbersSequenceRepository.findByAccountType("SAVINGS");
        assertNotNull(sequence);
        assertEquals("SAVINGS", sequence.getAccountType());
        assertEquals(0L, sequence.getCurrent());
    }

    @Test
    void createAccountTypeCounterTwiceSuccessTest() {
        accountNumbersSequenceRepository.createAccountTypeCounter("DEBIT");
        accountNumbersSequenceRepository.createAccountTypeCounter("DEBIT");

        assertEquals(1, accountNumbersSequenceRepository.findAll().size());
    }

    @Test
    void incrementCounter_incrementsCounterWhenExpectedValueMatches() {
        accountNumbersSequenceRepository.createAccountTypeCounter("SAVINGSS");
        AccountNumbersSequence sequence = accountNumbersSequenceRepository.findByAccountType("SAVINGSS");
        assertNotNull(sequence);

        boolean incremented = accountNumbersSequenceRepository.incrementCounter("SAVINGSS", 0L);

        Assertions.assertTrue(incremented);
        AccountNumbersSequence updatedSequence = accountNumbersSequenceRepository.findByAccountType("SAVINGSS");
        assertEquals(1L, updatedSequence.getCurrent());
    }

    @Test
    void incrementCounter_doesNotIncrementWhenExpectedValueDoesNotMatch() {
        accountNumbersSequenceRepository.createAccountTypeCounter("SAVINGSS");
        AccountNumbersSequence sequence = accountNumbersSequenceRepository.findByAccountType("SAVINGSS");
        assertNotNull(sequence);

        boolean incremented = accountNumbersSequenceRepository.incrementCounter("SAVINGSS", 1L);

        assertFalse(incremented);
        AccountNumbersSequence updatedSequence = accountNumbersSequenceRepository.findByAccountType("SAVINGSS");
        assertEquals(0L, updatedSequence.getCurrent());
    }


    @Test
    void incrementCounterWithOptimisticLockExceptionSuccessTest() {
        accountNumbersSequenceRepository.createAccountTypeCounter("SAVINGS");
        AccountNumbersSequence sequence = accountNumbersSequenceRepository.findByAccountType("SAVINGS");
        assertNotNull(sequence);
        sequence.setCurrent(1L);
        accountNumbersSequenceRepository.save(sequence);

        boolean incremented = accountNumbersSequenceRepository.incrementCounter("SAVINGS", 1L);

        Assertions.assertTrue(incremented);
        AccountNumbersSequence updatedSequence = accountNumbersSequenceRepository.findByAccountType("SAVINGS");
        assertEquals(2L, updatedSequence.getCurrent());
    }
}
