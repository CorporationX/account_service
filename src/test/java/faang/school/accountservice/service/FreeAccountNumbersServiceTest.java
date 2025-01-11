package faang.school.accountservice.service;

import faang.school.accountservice.entity.AccountNumbersSequence;
import faang.school.accountservice.repository.AccountNumbersSequenceRepository;
import faang.school.accountservice.services.FreeAccountNumbersService;
import faang.school.accountservice.util.BaseContextTest;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.function.Consumer;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;


public class FreeAccountNumbersServiceTest extends BaseContextTest {

    @Autowired
    private FreeAccountNumbersService freeAccountNumbersService;

    @Autowired
    private AccountNumbersSequenceRepository accountNumbersSequenceRepository;

    @BeforeAll
    static void setup() {
        Flyway flyway = Flyway.configure()
                .dataSource(POSTGRESQL_CONTAINER.getJdbcUrl(), POSTGRESQL_CONTAINER.getUsername(), POSTGRESQL_CONTAINER.getPassword())
                .load();
        flyway.migrate();
    }

    @BeforeEach
    void seedDatabase() throws SQLException {
        try (Connection connection = DriverManager.getConnection(
                POSTGRESQL_CONTAINER.getJdbcUrl(),
                POSTGRESQL_CONTAINER.getUsername(),
                POSTGRESQL_CONTAINER.getPassword())) {
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
        assertTrue(POSTGRESQL_CONTAINER.isRunning());
    }

    @Test
    void testGetFreeAccountNumberSuccessTest() {
        String accountType = "SAVING";
        Long existingAccountNumber = 523600000000001L;

        Consumer<Long> action = mock(Consumer.class);
        Long result = freeAccountNumbersService.getFreeAccountNumberWithTransaction(accountType, action);
        assertEquals(existingAccountNumber, result);
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
        Consumer<Long> action = mock(Consumer.class);
        freeAccountNumbersService.getFreeAccountNumberWithTransaction(accountType, action);
        AccountNumbersSequence sequence = accountNumbersSequenceRepository.findByAccountType(accountType);
        assertEquals(2L, sequence.getCurrent());
    }

    @AfterEach
    void cleanupDatabase() throws SQLException {
        try (Connection connection = DriverManager.getConnection(
                POSTGRESQL_CONTAINER.getJdbcUrl(),
                POSTGRESQL_CONTAINER.getUsername(),
                POSTGRESQL_CONTAINER.getPassword())) {
            try (PreparedStatement statement = connection.prepareStatement("DELETE FROM account_number_sequence")) {
                statement.executeUpdate();
            }
        }
    }
}
