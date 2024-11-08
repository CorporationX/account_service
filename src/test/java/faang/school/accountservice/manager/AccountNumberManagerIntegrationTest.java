package faang.school.accountservice.manager;

import faang.school.accountservice.enums.AccountNumberType;
import faang.school.accountservice.model.number.AccountUniqueNumberCounter;
import faang.school.accountservice.repository.AccountNumbersSequenceRepository;
import faang.school.accountservice.repository.FreeAccountNumbersRepository;
import faang.school.accountservice.service.account.numbers.AccountNumberConfig;
import faang.school.accountservice.service.account.numbers.AccountNumbersManager;
import faang.school.accountservice.util.BaseContextTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static java.util.concurrent.TimeUnit.SECONDS;

@Slf4j
@Transactional
public class AccountNumberManagerIntegrationTest extends BaseContextTest {

    @Autowired
    private AccountNumbersSequenceRepository accountNumbersSequenceRepository;

    @Autowired
    private AccountNumberConfig accountNumberConfig;

    @Autowired
    private FreeAccountNumbersRepository freeAccountNumbersRepository;

    @Autowired
    private AccountNumbersManager accountNumbersManager;

    @Test
    public void testMultithreadedQueries_AllTypeAccountNumbers() throws InterruptedException {
        int countRequest = getCountRequest();
        runMultithreadedTest(countRequest, AccountNumberType.values());
    }

    @Test
    public void testMultithreadedQueries_SingleTypeAccountNumbers() throws InterruptedException {
        int countRequest = getCountRequest();
        runMultithreadedTest(countRequest, AccountNumberType.BUSINESS);
    }

    @Test
    public void testOneRequest() throws InterruptedException {
        AccountNumberType type = AccountNumberType.BUSINESS;
        System.out.println("Start");
        accountNumbersManager.getAccountNumberAndApply(type, Assertions::assertNotNull);
        SECONDS.sleep(5L);
    }

    private void runMultithreadedTest(int countRequest, AccountNumberType... accountTypes) throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(accountTypes.length);
        Map<AccountNumberType, Set<String>> counterMap = new ConcurrentHashMap<>();

        submitAccountNumberRequests(executorService, countRequest, counterMap, accountTypes);

        shutdownExecutorService(executorService);

        StringBuilder message = new StringBuilder();
        processResults(counterMap, countRequest, message, accountTypes);

        System.out.println(message);
        printCounterResults(counterMap);
    }

    private int getCountRequest() {
        return accountNumberConfig.getBatchSize();
    }

    private void submitAccountNumberRequests(ExecutorService executorService, long countRequest, Map<AccountNumberType, Set<String>> counterMap, AccountNumberType... types) {
        for (AccountNumberType type : types) {
            executorService.submit(() -> {
                for (int i = 0; i < countRequest; i++) {
                    accountNumbersManager.getAccountNumberAndApply(type, accountNumberEntity -> {
                        counterMap.compute(type, (k, v) -> {
                            if (v == null) {
                                v = new HashSet<>();
                            }
                            v.add(accountNumberEntity.getDigitSequence());
                            return v;
                        });
                    });
                }
            });
        }
    }

    private void shutdownExecutorService(ExecutorService executorService) throws InterruptedException {
        executorService.shutdown();
        if (!executorService.awaitTermination(5, TimeUnit.MINUTES)) {
            executorService.shutdownNow();
        }
    }

    private void processResults(Map<AccountNumberType, Set<String>> counterMap, int countRequest, StringBuilder message, AccountNumberType... types) {
        for (AccountNumberType type : types) {
            long counterValue = accountNumbersSequenceRepository.findById(type.toString())
                    .map(AccountUniqueNumberCounter::getCounter).get();

            long freeAccountsCount = freeAccountNumbersRepository.countFreeAccountNumberByIdType(type);

            message.append(String.format("Type: %s, Final Counter Value: %d, Free Accounts Count: %d%n",
                    type, counterValue, freeAccountsCount));

        }
    }

    private void printCounterResults(Map<AccountNumberType, Set<String>> counterMap) {
        counterMap.forEach((k, v) -> {
            System.out.println(k + " " + (accountNumberConfig.getBatchSize()) + " requests " + v.size() + " unique responses");
        });
    }
}
