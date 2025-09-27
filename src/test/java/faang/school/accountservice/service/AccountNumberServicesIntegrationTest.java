package faang.school.accountservice.service;

import faang.school.accountservice.config.context.AccountGenerationConfig;
import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.util.BaseContextTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@Sql(scripts = "classpath:test/cleanup.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class AccountNumberServicesIntegrationTest extends BaseContextTest {

    @Autowired
    private AccountNumberProvisioningService provisioningService;

    @Autowired
    private FreeAccountNumberPoolService poolService;

    @Autowired
    private AccountNumberBatchProcessor batchProcessor;

    @Autowired
    private AccountSequenceService sequenceService;

    @Autowired
    private AccountGenerationConfig config;

    @BeforeEach
    void setUp() {
        for (AccountType type : AccountType.values()) {
            sequenceService.initialize(type);
        }
    }

    @Test
    @DisplayName("Should handle full cycle: generate, pool, provision")
    void shouldHandleFullCycle() {
        AccountType type = AccountType.DEBIT;

        batchProcessor.generateAndAddToPool(type, 10);
        assertThat(poolService.countAvailable(type)).isEqualTo(10);

        String number1 = provisioningService.provideAccountNumber(type);
        assertThat(number1).isNotNull();
        assertThat(poolService.countAvailable(type)).isEqualTo(9);

        provisioningService.ensurePoolCapacity(type, 20);
        assertThat(poolService.countAvailable(type)).isGreaterThanOrEqualTo(20);
    }

    @Test
    @DisplayName("Should handle concurrent provisioning")
    void shouldHandleConcurrentProvisioning() throws InterruptedException {
        AccountType type = AccountType.CREDIT;
        int threadCount = 10;
        int numbersPerThread = 5;

        batchProcessor.generateAndAddToPool(type, 20);

        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        Set<String> provisionedNumbers = Collections.synchronizedSet(new HashSet<>());

        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    for (int j = 0; j < numbersPerThread; j++) {
                        String number = provisioningService.provideAccountNumber(type);
                        provisionedNumbers.add(number);
                    }
                } finally {
                    latch.countDown();
                }
            });
        }

        assertThat(latch.await(30, TimeUnit.SECONDS)).isTrue();
        executor.shutdown();

        assertThat(provisionedNumbers).hasSize(threadCount * numbersPerThread);
    }
}