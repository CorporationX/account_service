package faang.school.accountservice.service.account;

import faang.school.accountservice.enums.account.AccountType;
import faang.school.accountservice.model.FreeAccountNumber;
import faang.school.accountservice.repository.AccountNumbersSequenceRepository;
import faang.school.accountservice.repository.FreeAccountNumberRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigInteger;
import static org.assertj.core.api.Assertions.*;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Testcontainers
@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.yaml")
@Transactional
class FreeAccountNumberServiceTest {
    private static final AccountType ACCOUNT_TYPE = AccountType.CHECKING;

    @Container
    private static final PostgreSQLContainer POSTGRES_CONTAINER =
            new PostgreSQLContainer<>("postgres:latest")
                    .withDatabaseName("testdb")
                    .withUsername("testUser")
                    .withPassword("testPassword");

    @Autowired
    private FreeAccountNumberService freeAccountNumberService;

    @Autowired
    private FreeAccountNumberRepository freeAccountNumberRepository;

    @Autowired
    private AccountNumbersSequenceRepository accountNumbersSequenceRepository;

    @BeforeAll
    public static void setup() {
        String postgresUrl = POSTGRES_CONTAINER.getJdbcUrl();
        String postgresUsername = POSTGRES_CONTAINER.getUsername();
        String postgresPassword = POSTGRES_CONTAINER.getPassword();
        System.setProperty("spring.datasource.url", postgresUrl);
        System.setProperty("spring.datasource.username", postgresUsername);
        System.setProperty("spring.datasource.password", postgresPassword);
    }

    @Test
    public void testGetFreeNumber() {
        BigInteger freeNumber = freeAccountNumberService.getFreeNumber(ACCOUNT_TYPE);
        assertThat(freeNumber).isNotNull();
    }

    @Test
    public void testGenerateFreeAccount() {
        FreeAccountNumber freeAccountNumber = freeAccountNumberService.generateFreeAccount(ACCOUNT_TYPE);
        assertThat(freeAccountNumber).isNotNull();
        assertThat(freeAccountNumber.getAccountNumber()).isNotNull();
    }

    @Test
    public void testCountFreeAccountNumbersByType() {
        int expectedCount = 7;
        generateFreeAccountNumbers(2, ACCOUNT_TYPE);
        BigInteger count = freeAccountNumberService.countFreeAccountNumbersByType(ACCOUNT_TYPE);
        assertThat(BigInteger.valueOf(expectedCount)).isEqualTo(count);
    }

    private void generateFreeAccountNumbers(int quantity, AccountType accountType) {
        for (int i = 0; i < quantity; i++) {
            freeAccountNumberService.generateFreeAccount(accountType);
        }
    }
}