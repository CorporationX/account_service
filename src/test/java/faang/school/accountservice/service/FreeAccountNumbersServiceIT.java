package faang.school.accountservice.service;

import faang.school.accountservice.entity.FreeAccountNumber;
import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.exception.AccountNumberNotFoundException;
import faang.school.accountservice.exception.InvalidBatchSizeException;
import faang.school.accountservice.repository.AccountNumbersSequenceRepository;
import faang.school.accountservice.repository.FreeAccountNumbersRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
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
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Testcontainers
@Transactional
@Slf4j
public class FreeAccountNumbersServiceIT {
    @Autowired
    private FreeAccountNumbersService freeAccountNumbersService;
    @Autowired
    private FreeAccountNumbersRepository freeAccountNumbersRepository;
    @Autowired
    private AccountNumbersSequenceRepository accountNumbersSequenceRepository;

    @Container
    public static PostgreSQLContainer<?> POSTGRESQL_CONTAINER
            = new PostgreSQLContainer<>("postgres:13.6");

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRESQL_CONTAINER::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRESQL_CONTAINER::getUsername);
        registry.add("spring.datasource.password", POSTGRESQL_CONTAINER::getPassword);
    }

    @BeforeEach
    void setUp() {
        accountNumbersSequenceRepository.findByAccountType(AccountType.CREDIT).setCounter(0L);
        accountNumbersSequenceRepository.flush();
        freeAccountNumbersRepository.deleteAll();
        freeAccountNumbersRepository.flush();
    }

    @Test
    public void testPositiveGeneratedAccountNumbers() {
        freeAccountNumbersService.generatedAccountNumbers(AccountType.CREDIT, 10);
        List<FreeAccountNumber> freeAccountNumber = freeAccountNumbersRepository.findAll();

        assertEquals(10, freeAccountNumber.size());
        assertEquals(4200_0000_0000_0001L, freeAccountNumber.get(1).getFreeAccountId().getAccountNumber());
    }

    @Test
    public void testPositiveGeneratedAccount() throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(10);
        for (int i = 0; i < 10; i++) {
            executor.submit(() -> {
                freeAccountNumbersService.generatedAccountNumbers(AccountType.CREDIT, 10);
            });
        }
        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.MINUTES);

        List<FreeAccountNumber> freeAccountNumbers = freeAccountNumbersRepository.findAll();

        assertEquals(freeAccountNumbers.size(), 100);

        Set<Long> uniqueAccountNumbers = freeAccountNumbers.stream()
                .map(number -> number.getFreeAccountId().getAccountNumber())
                .collect(Collectors.toSet());
        assertEquals(uniqueAccountNumbers.size(), 100, "Account numbers should be unique");
    }

    @Test
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

    @Test
    public void testNegativeGeneratedAccountNumbersBatchSize0() {
        assertThrows(InvalidBatchSizeException.class, () -> {
            freeAccountNumbersService.generatedAccountNumbers(AccountType.CREDIT, 0);
        });
    }

    @Test
    public void testNegativeGeneratedAccountNumbersBatchSize() {
        assertThrows(InvalidBatchSizeException.class, () -> {
            freeAccountNumbersService.generatedAccountNumbers(AccountType.DEBIT, -6);
        });
    }

    @Test
    public void testNegativeGeneratedAccountNumbersTypeNull() {
        assertThrows(NullPointerException.class, () -> {
            freeAccountNumbersService.generatedAccountNumbers(null, 10);
        });
    }

    @Test
    public void testNegativeRetrieveAccountNumberTypeNull() {
        assertThrows(NullPointerException.class, () -> {
            freeAccountNumbersService.retrieveAccountNumber(null, Object::notify);
        });
    }

    @Test
    public void testNegativeRetrieveAccountNumber() {
        assertThrows(AccountNumberNotFoundException.class, () -> {
            freeAccountNumbersService
                    .retrieveAccountNumber(AccountType.CREDIT, number -> {
                    });

        });
    }
}

