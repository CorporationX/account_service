package faang.school.accountservice.service;

import faang.school.accountservice.entity.FreeAccountId;
import faang.school.accountservice.entity.FreeAccountNumber;
import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.repository.AccountNumbersSeqRepository;
import faang.school.accountservice.repository.FreeAccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Testcontainers
@ActiveProfiles("test")
public class FreeAccountNumberServiceIntegrationTest {
    @Autowired
    FreeAccountRepository freeAccountRepository;
    @Autowired
    AccountNumbersSeqRepository accountNumbersSeqRepository;
    @Autowired
    FreeAccountNumberService service;
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:13.3")
            .withInitScript("initFreeAccNumsService.sql");

    static final long DEBIT_PATTERN = 4200_0000_0000_0000L;
    static final long SAVINGS_PATTERN = 5236_0000_0000_0000L;
    int batchSize;
    long initial;

    @BeforeEach
    void setUp() {
        batchSize = 5;
        initial = 1L;

        freeAccountRepository.deleteAll();
        accountNumbersSeqRepository.deleteAll();
    }

    @DynamicPropertySource
    static void postgresqlProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Test
//    @Transactional
    void testGenerateDebitNumbers() {
        testGenerateNumbers(AccountType.DEBIT, DEBIT_PATTERN);
    }

    @Test
    void testGenerateSavingsNumbers() {
        testGenerateNumbers(AccountType.SAVINGS, SAVINGS_PATTERN);
    }

    @Test
    void testRetrieveAccountNumber() {
        freeAccountRepository.save(new FreeAccountNumber(new FreeAccountId(AccountType.DEBIT, DEBIT_PATTERN + 1)));
        freeAccountRepository.save(new FreeAccountNumber(new FreeAccountId(AccountType.DEBIT, DEBIT_PATTERN + 2)));
        freeAccountRepository.save(new FreeAccountNumber(new FreeAccountId(AccountType.DEBIT, DEBIT_PATTERN + 3)));
        long initialDebitDigits = DEBIT_PATTERN / 1_0000_0000_0000L;
        long[] accountNumber = new long[1];
        Consumer<FreeAccountNumber> consumer = number -> accountNumber[0] = number.getId().getAccountNumber();

        service.retrieveAccountNumber(AccountType.DEBIT, consumer);
        assertEquals(initialDebitDigits, accountNumber[0] / 1_0000_0000_0000L);
        assertFalse(freeAccountRepository.existsById(new FreeAccountId(AccountType.DEBIT, accountNumber[0])));
    }

    void testGenerateNumbers(AccountType type, long pattern) {
        List<FreeAccountNumber> expectedNumbers = new ArrayList<>();
        for (long i = initial; i < initial + batchSize; i++) {
            expectedNumbers.add(new FreeAccountNumber(new FreeAccountId(type, pattern + i)));
        }

        service.generateAccountNumbers(type, batchSize);
        assertEquals(expectedNumbers, freeAccountRepository.findAll());
        assertEquals(batchSize + 1, accountNumbersSeqRepository.findById(type).get().getCounter());
    }
}


