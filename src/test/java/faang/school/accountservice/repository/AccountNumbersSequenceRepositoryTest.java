package faang.school.accountservice.repository;

import faang.school.accountservice.AccountServiceApplication;
import faang.school.accountservice.entity.AccountNumbersSequence;
import jakarta.transaction.Transactional;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
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

@SpringBootTest(classes = AccountServiceApplication.class)
@Testcontainers
@AutoConfigureMockMvc
public class AccountNumbersSequenceRepositoryTest {

    @Container
    static PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres:latest")
            .withDatabaseName("testdb")
            .withUsername("testuser")
            .withPassword("testpass");


    @Autowired
    private AccountNumbersSequenceRepository accountNumbersSequenceRepository;


    @DynamicPropertySource
    static void setDatasourceProperties(org.springframework.test.context.DynamicPropertyRegistry registry) {
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

    @Test
    void createNewAccountNumbersSequenceSuccessTest() {
        String accountType = "CHECKING";
        accountNumbersSequenceRepository.createAccountTypeCounter(accountType);
        AccountNumbersSequence sequence = accountNumbersSequenceRepository.findByAccountType(accountType);
        assertEquals(accountType, sequence.getAccountType());
    }

    @Test
    @Transactional
    @Rollback
    void createAccountTypeCounterSuccessTest() {
        accountNumbersSequenceRepository.createAccountTypeCounter("SAVING");

        AccountNumbersSequence sequence = accountNumbersSequenceRepository.findByAccountType("SAVING");
        assertNotNull(sequence);
        assertEquals("SAVING", sequence.getAccountType());
        assertEquals(0L, sequence.getCurrent());
    }

    @Test
    @Transactional
    @Rollback
    void createAccountTypeCounterTwiceSuccessTest() {
        accountNumbersSequenceRepository.createAccountTypeCounter("DEBIT");
        accountNumbersSequenceRepository.createAccountTypeCounter("DEBIT");

        assertEquals(1, accountNumbersSequenceRepository.findAll().size());
    }

    @Test
    @Transactional
    @Rollback
    void incrementCounter_incrementsCounterWhenExpectedValueMatches() {
        accountNumbersSequenceRepository.createAccountTypeCounter("SAVINGS");
        AccountNumbersSequence sequence = accountNumbersSequenceRepository.findByAccountType("SAVINGS");
        assertNotNull(sequence);

        boolean incremented = accountNumbersSequenceRepository.incrementCounter("SAVINGS", 0L);

        assertTrue(incremented);
        AccountNumbersSequence updatedSequence = accountNumbersSequenceRepository.findByAccountType("SAVINGS");
        assertEquals(1L, updatedSequence.getCurrent());
    }

    @Test
    @Transactional
    @Rollback
    void incrementCounter_doesNotIncrementWhenExpectedValueDoesNotMatch() {
        accountNumbersSequenceRepository.createAccountTypeCounter("SAVINGS");
        AccountNumbersSequence sequence = accountNumbersSequenceRepository.findByAccountType("SAVINGS");
        assertNotNull(sequence);

        boolean incremented = accountNumbersSequenceRepository.incrementCounter("SAVINGS", 1L);

        assertFalse(incremented);
        AccountNumbersSequence updatedSequence = accountNumbersSequenceRepository.findByAccountType("SAVINGS");
        assertEquals(0L, updatedSequence.getCurrent());
    }


    @Test
    @Transactional
    @Rollback
    void incrementCounterWithOptimisticLockExceptionSuccessTest() {
        accountNumbersSequenceRepository.createAccountTypeCounter("SAVING");
        AccountNumbersSequence sequence = accountNumbersSequenceRepository.findByAccountType("SAVING");
        assertNotNull(sequence);
        sequence.setCurrent(1L);
        accountNumbersSequenceRepository.save(sequence);

        boolean incremented = accountNumbersSequenceRepository.incrementCounter("SAVING", 1L);

        assertTrue(incremented);
        AccountNumbersSequence updatedSequence = accountNumbersSequenceRepository.findByAccountType("SAVING");
        assertEquals(2L, updatedSequence.getCurrent());
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
