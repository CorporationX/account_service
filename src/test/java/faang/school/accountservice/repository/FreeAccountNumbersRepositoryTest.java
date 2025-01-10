package faang.school.accountservice.repository;

import faang.school.accountservice.AccountServiceApplication;
import jakarta.transaction.Transactional;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
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

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = AccountServiceApplication.class)
@Testcontainers
@AutoConfigureMockMvc
public class FreeAccountNumbersRepositoryTest {

    @Container
    static PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres:latest")
            .withDatabaseName("testdb")
            .withUsername("testuser")
            .withPassword("testpass");


    @Autowired
    private FreeAccountNumbersRepository freeAccountNumbersRepository;

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
    @Transactional
    @Rollback
    void saveNewFreeAccountNumberSuccessTest() {
        String accountType = "SAVING";
        Long accountNumber = 523600000000001L;

        freeAccountNumbersRepository.saveNewFreeAccountNumber(accountType, accountNumber);

        Long retrievedAccountNumber = freeAccountNumbersRepository.getAndRemoveFirstFreeAccountNumber(accountType);
        assertNotNull(retrievedAccountNumber);
        assertEquals(accountNumber, retrievedAccountNumber);
    }

    @Test
    @Transactional
    @Rollback
    void getAndRemoveFreeAccountNumbersSuccessTest() {
        String accountType = "SAVING";
        Long accountNumber1 = 523600000000001L;
        Long accountNumber2 = 523600000000002L;
        freeAccountNumbersRepository.saveNewFreeAccountNumber(accountType, accountNumber1);
        freeAccountNumbersRepository.saveNewFreeAccountNumber(accountType, accountNumber2);

        Long firstAccount = freeAccountNumbersRepository.getAndRemoveFirstFreeAccountNumber(accountType);
        Long secondAccount = freeAccountNumbersRepository.getAndRemoveFirstFreeAccountNumber(accountType);
        assertEquals(accountNumber1, firstAccount);
        assertEquals(accountNumber2, secondAccount);
    }

    @Test
    void getAndRemoveFreeAccountNumberWithWrongAccountTypeSuccessTest() {
        String accountType = "INVALID";
        Long firstAccount = freeAccountNumbersRepository.getAndRemoveFirstFreeAccountNumber(accountType);
        assertNull(firstAccount);
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
