package faang.school.accountservice.service;

import faang.school.accountservice.AccountServiceApplication;
import faang.school.accountservice.entity.AccountNumbersSequence;
import faang.school.accountservice.repository.AccountNumbersSequenceRepository;
import faang.school.accountservice.services.FreeAccountNumbersService;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@SpringBootTest(classes = AccountServiceApplication.class)
@Testcontainers
@AutoConfigureMockMvc
public class FreeAccountNumbersServiceTest {

    @Container
    static PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres:latest")
            .withDatabaseName("testdb")
            .withUsername("testuser")
            .withPassword("testpass");


    @Autowired
    private FreeAccountNumbersService freeAccountNumbersService;

    @Autowired
    private AccountNumbersSequenceRepository accountNumbersSequenceRepository;

    @DynamicPropertySource
    static void setDatasourceProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgresContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgresContainer::getUsername);
        registry.add("spring.datasource.password", postgresContainer::getPassword);
    }


    @BeforeAll
    static void setup() {
        Flyway flyway = Flyway.configure()
                .dataSource(postgresContainer.getJdbcUrl(), postgresContainer.getUsername(), postgresContainer.getPassword())
                .load();
        flyway.migrate();
    }

    @BeforeEach
    void seedDatabase() throws SQLException {
        try (Connection connection = DriverManager.getConnection(
                postgresContainer.getJdbcUrl(),
                postgresContainer.getUsername(),
                postgresContainer.getPassword())) {
            try (PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO account_number_sequence (account_type, current) VALUES (?, ?)")) {
                statement.setString(1, "SAVING");
                statement.setLong(2, 1L);
                statement.executeUpdate();
            }
        }
    }

    @Test
    void testContainerStartup() {
        assertTrue(postgresContainer.isRunning());
    }

    @Test
    void testGetFreeAccountNumberSuccessTest() {
        String accountType = "SAVING";
        Long existingAccountNumber = 523600000000001L;

        freeAccountNumbersService.createNewFreeAccountNumber(accountType);
        Runnable action = mock(Runnable.class);

        Long result = freeAccountNumbersService.getFreeAccountNumberWithTransaction(accountType, action);

        assertEquals(existingAccountNumber, result);
        verify(action).run();
    }


    @Test
    void createNewFreeAccountNumberWrongAccountTypeFailedTest() {
        String accountType = "INVALID";

        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                freeAccountNumbersService.createNewFreeAccountNumber(accountType));

        assertEquals("Invalid account type: " + accountType, exception.getMessage());
    }

    @Test
    void createNewAccountNumbersSequenceSuccessTest() {
        String accountType = "CHECKING";
        freeAccountNumbersService.createNewAccountNumbersSequence(accountType);
        AccountNumbersSequence sequence = accountNumbersSequenceRepository.findByAccountType(accountType);
        assertNotNull(sequence);
        assertEquals("CHECKING", sequence.getAccountType());
        assertEquals(0L, sequence.getCurrent());
    }

    @Test
    void createNewAccountNumbersSequenceFailedTest() {
        String accountType = "CHECKING";

        accountNumbersSequenceRepository.createAccountTypeCounter(accountType);
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                freeAccountNumbersService.createNewAccountNumbersSequence(accountType));

        assertEquals("AccountNumbersSequence for account type " + accountType + " is exist", exception.getMessage());
    }

    @Test
    void getFreeAccountNumberWithTransactionSuccessTest() {

        String accountType = "SAVING";

        freeAccountNumbersService.getFreeAccountNumberWithTransaction(accountType, () -> {
        });
        AccountNumbersSequence sequence = accountNumbersSequenceRepository.findByAccountType(accountType);
        assertEquals(2L, sequence.getCurrent());
    }

    @AfterEach
    void cleanupDatabase() throws SQLException {
        try (Connection connection = DriverManager.getConnection(
                postgresContainer.getJdbcUrl(),
                postgresContainer.getUsername(),
                postgresContainer.getPassword())) {
            try (PreparedStatement statement = connection.prepareStatement("DELETE FROM account_number_sequence")) {
                statement.executeUpdate();
            }
        }
    }
}
