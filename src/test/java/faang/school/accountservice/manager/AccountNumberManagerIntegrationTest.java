package faang.school.accountservice.manager;

import faang.school.accountservice.enums.AccountNumberType;
import faang.school.accountservice.model.number.AccountUniqueNumberCounter;
import faang.school.accountservice.model.number.FreeAccountNumber;
import faang.school.accountservice.repository.AccountNumbersSequenceRepository;
import faang.school.accountservice.repository.FreeAccountNumbersRepository;
import faang.school.accountservice.service.account.numbers.AccountNumberConfig;
import faang.school.accountservice.service.account.numbers.AccountNumbersManager;
import faang.school.accountservice.util.BaseContextTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AccountNumberManagerIntegrationTest extends BaseContextTest {

    @Autowired
    private AccountNumbersSequenceRepository accountNumbersSequenceRepository;

    @Autowired
    private AccountNumberConfig accountNumberConfig;

    @Autowired
    private FreeAccountNumbersRepository freeAccountNumbersRepository;

    @Autowired
    private AccountNumbersManager accountNumbersManager;


    @BeforeEach
    void setUp() {
    }

    @Test
    public void testAccountNumberIncrementAndRetrieval() throws InterruptedException {
        long COUNT_REQUEST = 10L * accountNumberConfig.getMaxNumberOfFreeAccounts();

        ExecutorService executorService = Executors.newFixedThreadPool(AccountNumberType.values().length);

        for (AccountNumberType type : AccountNumberType.values()) {
            executorService.submit(() -> {
                for (int i = 0; i < COUNT_REQUEST; i++) {
                    accountNumbersManager.getAccountNumberAndApply(type, FreeAccountNumber::getDigitSequence);
                }
            });
        }

        executorService.shutdown();
        if (!executorService.awaitTermination(10, TimeUnit.MINUTES)) {
            executorService.shutdownNow();
        }

        for (AccountNumberType type : AccountNumberType.values()) {
            long counterValue = accountNumbersSequenceRepository.findById(type.toString())
                    .map(AccountUniqueNumberCounter::getCounter).get();

            long freeAccountsCount = freeAccountNumbersRepository.countFreeAccountNumberByType(type);

            System.out.printf("Type: %s, Final Counter Value: %d, Free Accounts Count: %d%n",
                    type, counterValue, freeAccountsCount);

            assertEquals(accountNumberConfig.getMaxNumberOfFreeAccounts() + COUNT_REQUEST, counterValue);
            assertEquals(accountNumberConfig.getMaxNumberOfFreeAccounts(), freeAccountsCount);
        }
    }
}
