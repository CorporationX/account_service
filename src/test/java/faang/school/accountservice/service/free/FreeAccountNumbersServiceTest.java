package faang.school.accountservice.service.free;

import faang.school.accountservice.entity.account.AccountNumbersSequence;
import faang.school.accountservice.entity.free.FreeAccountId;
import faang.school.accountservice.entity.free.FreeAccountNumber;
import faang.school.accountservice.enums.account.AccountType;
import faang.school.accountservice.repository.account.AccountNumbersSequenceRepository;
import faang.school.accountservice.repository.free.FreeAccountNumbersRepository;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ActiveProfiles("test")
@SpringBootTest
@Testcontainers
public class FreeAccountNumbersServiceTest {

    @Autowired
    private FreeAccountNumbersService freeAccountNumbersService;
    @Autowired
    private FreeAccountNumbersRepository accountNumbersRepository;

    @Container
    public static PostgreSQLContainer<?> POSTGRESQL_CONTAINER =
            new PostgreSQLContainer<>("postgres:13.3");


    @DynamicPropertySource
    public static void setPostgresqlProperties(DynamicPropertyRegistry registry) {
        POSTGRESQL_CONTAINER.start();
        POSTGRESQL_CONTAINER.waitingFor(Wait.forListeningPort());

        registry.add("spring.datasource.url", POSTGRESQL_CONTAINER::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRESQL_CONTAINER::getUsername);
        registry.add("spring.datasource.password", POSTGRESQL_CONTAINER::getPassword);

        registry.add("spring.liquibase.url", POSTGRESQL_CONTAINER::getJdbcUrl);
        registry.add("spring.liquibase.user", POSTGRESQL_CONTAINER::getUsername);
        registry.add("spring.liquibase.password", POSTGRESQL_CONTAINER::getPassword);
    }

    @Test
    void testGenerateAccountNumbers(){
        long previous = accountNumbersRepository.countByType(AccountType.INVESTMENT_ACCOUNT.name());
        int batchSize = 110;
        freeAccountNumbersService.generateAccountNumbers(AccountType.INVESTMENT_ACCOUNT, batchSize);
        long count = accountNumbersRepository.countByType(AccountType.INVESTMENT_ACCOUNT.name());
        assertEquals(previous+batchSize, count);

    }

    @Test
    void testEnsureAccountNumbers(){
        int targetCount = 500;
        freeAccountNumbersService.ensureAccountNumbers(AccountType.INVESTMENT_ACCOUNT, targetCount);
        long count = accountNumbersRepository.countByType(AccountType.INVESTMENT_ACCOUNT.name());
        assertEquals(targetCount, count);
    }

    @Test
    void testGetFreeAccountNumber(){
        freeAccountNumbersService.generateAccountNumbers(AccountType.SAVING_ACCOUNT, 2);
        String freeAccountNumber = freeAccountNumbersService.getFreeAccountNumber(AccountType.SAVING_ACCOUNT);
        assertNotNull(freeAccountNumber);
//        assertEquals(1, accountNumbersRepository.countByType(AccountType.SAVING_ACCOUNT.name()));
    }

    @Test
    public void testSaveAllAccountNumbers() {
        FreeAccountNumber firstAccountNumber = new FreeAccountNumber(new FreeAccountId(
                AccountType.CHECKING_ACCOUNT_INDIVIDUAL,
                4200_0000_0000_0001L));
        FreeAccountNumber secondAccountNumber = new FreeAccountNumber(new FreeAccountId(
                AccountType.SAVING_ACCOUNT,
                5327_0000_0000_0001L));
        List<FreeAccountNumber> accountNumbers = List.of(firstAccountNumber, secondAccountNumber);

        freeAccountNumbersService.saveAllAccountNumbers(accountNumbers);
    }

    @Test
    public void testIncrementAndGetCounter() {
        AccountNumbersSequence accountNumbersSequence = freeAccountNumbersService.incrementAndGetCounter(
                "CHECKING_ACCOUNT_LEGAL_ENTITY", 500
        );

        assertEquals(501, accountNumbersSequence.getCounter());
        assertEquals(0, accountNumbersSequence.getInitialValue());
    }

    @Test
    void testValidateBatchSize() {
        // Тестируем на некорректном размере пакета
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> {
            freeAccountNumbersService.validateBatchSize(-1);
        });
        assertEquals("batchSize must be greater than 0", e.getMessage());
    }

    @Test
    void testValidateCounter() {
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> {
            freeAccountNumbersService.validateCounter(9999999999999L); // Превышает максимальное значение
        });
        assertEquals("Reached the limit of accounts numbers: 999999999999", e.getMessage());
    }

    @Test
    void testValidateTargetCount() {
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> {
            freeAccountNumbersService.validateTargetCount(9999999999999L); // Превышает максимальное значение
        });
        assertEquals("Reached the limit of accounts numbers: 999999999999", e.getMessage());
    }
}
