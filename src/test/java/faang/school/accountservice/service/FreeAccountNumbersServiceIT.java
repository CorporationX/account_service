package faang.school.accountservice.service;

import faang.school.accountservice.entity.AccountSeq;
import faang.school.accountservice.entity.FreeAccountNumber;
import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.repository.AccountNumbersSequenceRepository;
import faang.school.accountservice.repository.FreeAccountNumbersRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Testcontainers
@Transactional
public class FreeAccountNumbersServiceIT {
    @Autowired
    private FreeAccountNumbersService freeAccountNumbersService;
    @Autowired
    private FreeAccountNumbersRepository freeAccountNumbersRepository;
    @Autowired
    AccountNumbersSequenceRepository accountNumbersSequenceRepository;

    @Container
    public static PostgreSQLContainer<?> POSTGRESQL_CONTAINER
            = new PostgreSQLContainer<>("postgres:13.6");


    @Test
    @Rollback
    public void testPositiveGeneratedAccountNumbers() throws InterruptedException {
        freeAccountNumbersService.generatedAccountNumbers(AccountType.CREDIT, 10);
        Thread.sleep(1000);
        List<FreeAccountNumber> freeAccountNumbers = freeAccountNumbersRepository.findAll();
        assertEquals(freeAccountNumbers.size(), 10);
        assertEquals(4200_0000_0000_0001L, freeAccountNumbers.get(1).getFreeAccountId().getAccountNumber());
    }

    @Test
    @Rollback
    public void testPositiveGeneratedAccount() throws InterruptedException {

        ExecutorService executor = Executors.newFixedThreadPool(10);
        for (int i = 0; i < 10; i++) {
            executor.submit(() -> {
                freeAccountNumbersService.generatedAccountNumbers(AccountType.CREDIT, 100);
            });
        }
        executor.shutdown();
        boolean b = executor.awaitTermination(1, TimeUnit.MINUTES);

        List<FreeAccountNumber> freeAccountNumbers = freeAccountNumbersRepository.findAll();

        assertEquals(1000, freeAccountNumbers.size());

        Set<Long> uniqueAccountNumbers = freeAccountNumbers.stream()
                .map(number -> number.getFreeAccountId().getAccountNumber())
                .collect(Collectors.toSet());
        assertEquals(1000, uniqueAccountNumbers.size(), "Account numbers should be unique");
    }

    @Test
    @Rollback
    public void testPositiveRetrieveAccountNumber() {
        freeAccountNumbersService.generatedAccountNumbers(AccountType.CREDIT, 10);
        FreeAccountNumber accountNumber = freeAccountNumbersService
                .retrieveAccountNumber(AccountType.CREDIT, number -> {
                });
        List<FreeAccountNumber> freeAccountNumbers = freeAccountNumbersRepository.findAll();
        assertEquals(freeAccountNumbers.size(), 9);
        assertEquals(AccountType.CREDIT, accountNumber.getFreeAccountId().getAccountType());
        assertEquals(4200_0000_0000_0000L, accountNumber.getFreeAccountId().getAccountNumber());
    }

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRESQL_CONTAINER::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRESQL_CONTAINER::getUsername);
        registry.add("spring.datasource.password", POSTGRESQL_CONTAINER::getPassword);
    }

    @BeforeEach
    void setUp() {
        freeAccountNumbersRepository.deleteAll();
    }

    @AfterEach
    void tearDown() {
        freeAccountNumbersRepository.deleteAll();
    }
}
