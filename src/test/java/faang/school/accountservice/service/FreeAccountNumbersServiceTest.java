package faang.school.accountservice.service;

import faang.school.accountservice.exception.AbsentFreeAccException;
import faang.school.accountservice.model.AccountBalanceType;
import faang.school.accountservice.model.AccountNumberSequence;
import faang.school.accountservice.model.FreeAccountNumber;
import faang.school.accountservice.repository.AccountNumbersSequenceRepository;
import faang.school.accountservice.repository.FreeAccountNumbersRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigInteger;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Testcontainers
@ExtendWith(MockitoExtension.class)
class FreeAccountNumbersServiceTest {

    @Value("${debit.pattern}")
    private BigInteger debitPattern;
    @Value("${credit.pattern}")
    private BigInteger creditPattern;

    @Autowired
    private AccountNumbersSequenceRepository sequenceRepository;
    @Autowired
    private FreeAccountNumbersRepository freeNumbersRepository;
    @Autowired
    private FreeAccountNumbersService freeAccountNumbersService;

    @Container
    public static PostgreSQLContainer<?> POSTGRESQL_CONTAINER =
            new PostgreSQLContainer<>("postgres:13.6")
                    .withDatabaseName("testdb")
                    .withUsername("testuser")
                    .withPassword("testpass");

    @DynamicPropertySource
    static void postgresqlProperties(DynamicPropertyRegistry registry) {
        POSTGRESQL_CONTAINER.start();
        registry.add("spring.datasource.url", POSTGRESQL_CONTAINER::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRESQL_CONTAINER::getUsername);
        registry.add("spring.datasource.password", POSTGRESQL_CONTAINER::getPassword);
        registry.add("spring.datasource.driver-class-name", () -> "org.postgresql.Driver");

        registry.add("spring.liquibase.change-log", () -> "classpath:db/changelog/db.changelog-master.yaml");
        registry.add("spring.liquibase.enabled", () -> "true");
        registry.add("spring.liquibase.drop-first", () -> "true");

        registry.add("spring.jpa.hibernate.ddl-auto", () -> "none");
        registry.add("spring.jpa.show-sql", () -> "true");
        registry.add("spring.jpa.properties.hibernate.format_sql", () -> "true");

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @BeforeEach
    void setUp() {
        sequenceRepository.deleteAll();
        freeNumbersRepository.deleteAll();
    }

    @Test
    @Transactional
    void test_createOneFreeAccNumberPerType_WhenSequenceNotExists_ShouldCreateNewSequenceAndAccountNumber() {
        AccountBalanceType type = AccountBalanceType.DEBIT;
        freeAccountNumbersService.createOneFreeAccNumberPerType(type);

        Integer count = sequenceRepository.getIncrementedCountByBalanceType(type);
        assertEquals(count, 1);

        List<FreeAccountNumber> freeNumbers = freeNumbersRepository.findAll();
        assertThat(freeNumbers).hasSize(1);

        FreeAccountNumber createdNumber = freeNumbers.get(0);
        assertEquals(createdNumber.getAccountBalanceType(), type);
        assertEquals(createdNumber.getAccountNumber(), debitPattern.add(BigInteger.valueOf(1)).toString());
    }

    @Test
    @Transactional
    void test_createOneFreeAccNumberPerType_WhenSequenceExists_ShouldIncrementAndCreateAccountNumber() {
        AccountBalanceType type = AccountBalanceType.DEBIT;

        AccountNumberSequence existingSequence = new AccountNumberSequence();
        existingSequence.setAccountBalanceType(type);
        existingSequence.setAcountsCount(5);
        sequenceRepository.save(existingSequence);
        freeAccountNumbersService.createOneFreeAccNumberPerType(type);

        Integer count = sequenceRepository.getIncrementedCountByBalanceType(type);
        assertEquals(count, 6);

        List<FreeAccountNumber> freeNumbers = freeNumbersRepository.findAll();
        assertThat(freeNumbers).hasSize(1);

        FreeAccountNumber createdNumber = freeNumbers.get(0);
        assertThat(createdNumber.getAccountBalanceType()).isEqualTo(type);
        assertEquals(createdNumber.getAccountBalanceType(), type);
        assertEquals(createdNumber.getAccountNumber(), debitPattern.add(BigInteger.valueOf(6)).toString());
    }

    @Test
    @Transactional
    void test_createOneFreeAccNumberPerType_ForCreditType_ShouldUseCreditPattern() {
        AccountBalanceType type = AccountBalanceType.CREDIT;
        freeAccountNumbersService.createOneFreeAccNumberPerType(type);

        List<FreeAccountNumber> freeNumbers = freeNumbersRepository.findAll();
        assertThat(freeNumbers).hasSize(1);

        FreeAccountNumber createdNumber = freeNumbers.get(0);
        assertEquals(freeNumbers.get(0).getAccountBalanceType(), type);
        assertEquals(createdNumber.getAccountNumber(), creditPattern.add(BigInteger.valueOf(1)).toString());
    }

    @Test
    @Transactional
    void test_createOneFreeAccNumberPerType_ConcurrentCalls_ShouldHandleCorrectly() {
        AccountBalanceType type = AccountBalanceType.DEBIT;

        freeAccountNumbersService.createOneFreeAccNumberPerType(type);
        freeAccountNumbersService.createOneFreeAccNumberPerType(type);

        List<FreeAccountNumber> freeNumbers = freeNumbersRepository.findAll();
        assertThat(freeNumbers).hasSize(2);
        assertThat(freeNumbers.get(0).getAccountNumber())
                .isNotEqualTo(freeNumbers.get(1).getAccountNumber());
    }

    @Test
    @Transactional
    void test_getFreeAccNumberByType_WhenNoFreeNumbers_ShouldReturnException() {
        AccountBalanceType type = AccountBalanceType.DEBIT;
        AbsentFreeAccException absentFreeAccException = assertThrows(AbsentFreeAccException.class,
                () -> freeAccountNumbersService.getFreeAccNumberByType(type));
        assertEquals("No free account numbers available for type: DEBIT", absentFreeAccException.getMessage());
    }

    @Test
    @Transactional
    void test_getFreeAccNumberByType_WhenFreeNumbersExist_ShouldReturnFreeAccountNumberObject() {
        AccountBalanceType type = AccountBalanceType.CREDIT;
        freeAccountNumbersService.createOneFreeAccNumberPerType(type);

        assertEquals(1, freeNumbersRepository.getActualFreeNumCountByType(type));
        FreeAccountNumber freeAccountNumber = freeAccountNumbersService.getFreeAccNumberByType(type);
        assertEquals(freeAccountNumber.getAccountBalanceType(), type);
        assertEquals(freeAccountNumber.getAccountNumber(), creditPattern.add(BigInteger.valueOf(1)).toString());
    }

    @Test
    @Transactional
    void test_createQuantityOfNewAccNumbers_WhenQuantityIsZeroOrNegative_ShouldThrowException() {
        AccountBalanceType type = AccountBalanceType.DEBIT;
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> freeAccountNumbersService.createQuantityOfNewAccNumbers(type, 0));
        assertEquals("Quantity must be greater than 0", exception.getMessage());
    }

    @Test
    @Transactional
    void test_createQuantityOfNewAccNumbers_WhenQuantityIsOne_ShouldCreateOneAccountNumber() {
        AccountBalanceType type = AccountBalanceType.DEBIT;
        freeAccountNumbersService.createQuantityOfNewAccNumbers(type, 1);

        List<FreeAccountNumber> freeNumbers = freeNumbersRepository.findAll();
        assertThat(freeNumbers).hasSize(1);
        assertEquals(freeNumbers.get(0).getAccountBalanceType(), type);
        assertEquals(freeNumbers.get(0).getAccountNumber(), debitPattern.add(BigInteger.valueOf(1)).toString());
    }

    @Test
    @Transactional
    void test_createQuantityOfNewAccNumbers_WhenQuantityIsTen_ShouldCreateOneAccountNumber() {
        AccountBalanceType type = AccountBalanceType.CREDIT;
        freeAccountNumbersService.createQuantityOfNewAccNumbers(type, 10);

        List<FreeAccountNumber> freeNumbers = freeNumbersRepository.findAll();
        assertThat(freeNumbers).hasSize(10);
    }

    @Test
    @Transactional
    void test_createTargetQuantityOfAccNumbers_WhenTargetQuantityIsZeroOrNegative_ShouldThrowException() {
        AccountBalanceType type = AccountBalanceType.DEBIT;
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> freeAccountNumbersService.createTargetQuantityOfAccNumbers(type, 0));
        assertEquals("Target quantity must be greater than 0", exception.getMessage());
    }

    @Test
    @Transactional
    void test_createTargetQuantityOfAccNumbers_WhenTargetQuantityIsTenAndNowIsTwo_ShouldCreateTenAccountNumbers() {
        AccountBalanceType type = AccountBalanceType.DEBIT;
        freeAccountNumbersService.createOneFreeAccNumberPerType(type);
        freeAccountNumbersService.createOneFreeAccNumberPerType(type);

        assertEquals(2, freeNumbersRepository.getActualFreeNumCountByType(type));
        freeAccountNumbersService.createTargetQuantityOfAccNumbers(type, 10);

        List<FreeAccountNumber> freeNumbers = freeNumbersRepository.findAll();
        assertThat(freeNumbers).hasSize(10);
    }
}