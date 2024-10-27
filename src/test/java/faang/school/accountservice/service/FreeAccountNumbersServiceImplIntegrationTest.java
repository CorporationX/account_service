package faang.school.accountservice.service;

import faang.school.accountservice.entity.FreeAccountNumber;
import faang.school.accountservice.entity.FreeAccountNumberId;
import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.repository.FreeAccountNumbersRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@ActiveProfiles("test")
@Testcontainers
@Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, statements = """
        TRUNCATE free_account_number, account_numbers_sequence;
        """)
public class FreeAccountNumbersServiceImplIntegrationTest {

    @Container
    private static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:latest")
            .withInitScript("script/init_account_number.sql")
            .withDatabaseName("test")
            .withUsername("test")
            .withPassword("test");


    @DynamicPropertySource
    static void configureDatabase(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
    }

    @Autowired
    private FreeAccountNumbersServiceImpl freeAccountNumbersService;

    @Autowired
    private FreeAccountNumbersRepository freeAccountNumbersRepository;

    private String accountNumber;

    @BeforeEach
    void setUp() {
        accountNumber = "42000000000000000001";
    }

    @Test
    @Sql(statements = """
            INSERT INTO free_account_number (type, account_number)
            VALUES ('CHECKING_INDIVIDUAL', 42000000000000000001);
            """)
    public void testGenerateNumberByType_FoundNumber() {
        AccountType type = AccountType.CHECKING_INDIVIDUAL;

        String result = freeAccountNumbersService.generateNumberByType(type);

        assertNotNull(result);
        assertEquals(accountNumber, result);
    }

    @Test
    public void testGenerateNumberByType_NotFoundNumber() {
        AccountType type = AccountType.CHECKING_INDIVIDUAL;

        String result = freeAccountNumbersService.generateNumberByType(type);

        assertNotNull(result);
        assertEquals(accountNumber, result);
    }

    @Test
    @Sql(statements = """
            INSERT INTO free_account_number (type, account_number)
            VALUES ('CHECKING_INDIVIDUAL', 42000000000000000001);
            """)
    public void testGenerateNumberAndExecute() {
        AccountType type = AccountType.CHECKING_INDIVIDUAL;

        freeAccountNumbersService.generateNumberAndExecute(type, accountNumberEntity -> {
            assertNotNull(accountNumberEntity);
            assertEquals(type, accountNumberEntity.getId().getType());
        });
    }

    @Test
    public void testSaveNumber() {
        AccountType type = AccountType.CHECKING_INDIVIDUAL;
        FreeAccountNumber correctResult = FreeAccountNumber.builder()
                .id(new FreeAccountNumberId(type, accountNumber))
                .build();

        freeAccountNumbersService.saveNumber(type, accountNumber);
        List<FreeAccountNumber> result = freeAccountNumbersRepository.findAll();

        assertEquals(result.size(), 1);
        assertEquals(correctResult, result.get(0));
    }
}
