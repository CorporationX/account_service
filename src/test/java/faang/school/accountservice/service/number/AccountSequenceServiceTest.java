package faang.school.accountservice.service.number;

import faang.school.accountservice.config.AccountNumberProperties;
import faang.school.accountservice.entity.account.AccountSeq;
import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.repository.AccountNumbersSequenceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.*;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@Testcontainers
class AccountSequenceServiceTest {

    @Container
    static final PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:16-alpine")
                    .withDatabaseName("account_service_test")
                    .withUsername("test")
                    .withPassword("test");

    @DynamicPropertySource
    static void configureDatasource(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "update");
        registry.add("account.number.batch.size", () -> "10");
        registry.add("account.number.max-retries", () -> "100");
    }

    @Autowired
    private AccountSequenceService accountSequenceService;

    @Autowired
    private AccountNumbersSequenceRepository accountNumbersSequenceRepository;

    @Autowired
    private AccountNumberProperties accountNumberProperties;

    @BeforeEach
    void setUp() {
        accountNumbersSequenceRepository.deleteAll();
    }

    @Test
    @DisplayName("Single-thread increment should reserve correct range and update counter")
    void singleThreadIncrement_shouldReserveRangeAndUpdateCounter() {
        AccountType type = AccountType.CURRENT;

        accountNumbersSequenceRepository.save(new AccountSeq(type, 0L));

        AccountPeriod period = accountSequenceService.incrementCounter(type, 5);

        assertThat(period.fromInclusive()).isEqualTo(1L);
        assertThat(period.toInclusive()).isEqualTo(5L);

        AccountSeq seq = accountNumbersSequenceRepository.findById(type).orElseThrow();
        assertThat(seq.getCounter()).isEqualTo(5L);
    }

    @Test
    @DisplayName("Concurrent increments should produce non-overlapping contiguous ranges")
    void concurrentIncrement_shouldProduceNonOverlappingRanges() throws Exception {
        AccountType type = AccountType.SAVING;
        int threads = 10;
        int batchSize = 10;

        accountNumbersSequenceRepository.save(new AccountSeq(type, 0L));

        ExecutorService executor = Executors.newFixedThreadPool(threads);
        CompletionService<AccountPeriod> completionService =
                new ExecutorCompletionService<>(executor);

        for (int i = 0; i < threads; i++) {
            completionService.submit(() -> accountSequenceService.incrementCounter(type, batchSize));
        }

        List<AccountPeriod> periods = new ArrayList<>();
        for (int i = 0; i < threads; i++) {
            Future<AccountPeriod> future = completionService.take();
            periods.add(future.get(10, TimeUnit.SECONDS));
        }

        executor.shutdown();
        executor.awaitTermination(30, TimeUnit.SECONDS);

        assertThat(periods).hasSize(threads);

        periods.sort(Comparator.comparingLong(AccountPeriod::fromInclusive));

        long expectedFrom = 1L;
        for (AccountPeriod period : periods) {
            long from = period.fromInclusive();
            long to = period.toInclusive();

            assertThat(from).isEqualTo(expectedFrom);
            assertThat(to - from + 1).isEqualTo(batchSize);

            expectedFrom = to + 1;
        }

        long expectedFinalCounter = (long) threads * batchSize;

        AccountSeq seq = accountNumbersSequenceRepository.findById(type).orElseThrow();
        assertThat(seq.getCounter()).isEqualTo(expectedFinalCounter);
    }
}
