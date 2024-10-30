package faang.school.accountservice.scheduler;

import faang.school.accountservice.config.TestContainersConfig;
import faang.school.accountservice.model.AccountNumbersSequence;
import faang.school.accountservice.repository.AccountNumbersSequenceRepository;
import faang.school.accountservice.repository.FreeAccountNumbersRepository;
import faang.school.accountservice.service.FreeAccountNumberService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static faang.school.accountservice.enums.account.AccountType.CREDIT;
import static faang.school.accountservice.enums.account.AccountType.DEBIT;
import static faang.school.accountservice.enums.account.AccountType.SAVINGS;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ActiveProfiles("test")
@Testcontainers
@ContextConfiguration(classes = TestContainersConfig.class)
public class AccountNumberSchedulerTest extends TestContainersConfig {

    @Autowired
    private FreeAccountNumberService freeAccountNumberService;

    @Autowired
    private AccountNumberScheduler accountNumberScheduler;

    @Autowired
    private FreeAccountNumbersRepository freeAccountNumbersRepository;

    @Autowired
    private AccountNumbersSequenceRepository accountNumbersSequenceRepository;

    @Value("${account.number.batch_size}")
    private int batchSize;

    @AfterEach
    public void cleanUp() {
        freeAccountNumbersRepository.deleteAll();
        List<AccountNumbersSequence> sequences = accountNumbersSequenceRepository.findAll();
        sequences.stream()
                .peek(sequence -> sequence.setCurrentNumber(0L))
                .forEach(accountNumbersSequenceRepository::save);
    }

    @Test
    public void testGenerateAccountNumbers() {
        freeAccountNumberService.generateAccountNumbers(SAVINGS, 2, true);
        freeAccountNumberService.generateAccountNumbers(DEBIT, 1, true);

        accountNumberScheduler.generateAccountNumbers();

        int savingsCount = freeAccountNumberService.getQuantityFreeAccountNumbersByType(SAVINGS);
        int debitCount = freeAccountNumberService.getQuantityFreeAccountNumbersByType(DEBIT);
        int creditCount = freeAccountNumberService.getQuantityFreeAccountNumbersByType(CREDIT);

        List<AccountNumbersSequence> sequences = accountNumbersSequenceRepository.findAll();

        sequences.forEach(sequence -> assertEquals(batchSize, sequence.getCurrentNumber()));
        assertEquals(batchSize, savingsCount);
        assertEquals(batchSize, debitCount);
        assertEquals(batchSize, creditCount);
    }
}