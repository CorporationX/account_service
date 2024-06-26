package faang.school.accountservice.scheduler;

import faang.school.accountservice.enums.account.AccountType;
import faang.school.accountservice.service.account.FreeAccountNumberService;
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

import static org.assertj.core.api.Assertions.*;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Testcontainers
@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.yaml")
@Transactional
class FreeAccountNumberSchedulerTest {
    private static final AccountType ACCOUNT_TYPE = AccountType.CHECKING;

    @Container
    private static final PostgreSQLContainer POSTGRES_CONTAINER =
            new PostgreSQLContainer<>("postgres:latest")
                    .withDatabaseName("testdb")
                    .withUsername("testUser")
                    .withPassword("testPassword");

    @Autowired
    private FreeAccountNumberScheduler freeAccountNumberScheduler;

    @Autowired
    private FreeAccountNumberService freeAccountNumberService;

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
    public void generateNewAccounts_ShouldGenerateAccounts() {
        int initialCount = freeAccountNumberService.countFreeAccountNumbersByType(ACCOUNT_TYPE).intValue();
        freeAccountNumberScheduler.generateNewAccounts();
        int newCount = freeAccountNumberService.countFreeAccountNumbersByType(ACCOUNT_TYPE).intValue();
        assertThat(newCount).isEqualTo(initialCount + freeAccountNumberScheduler.getQuantityOneTimeInDayRefilling());
    }

    @Test
    public void generateMissingAccounts_ShouldGenerateMissingAccounts() {
        int initialCount = freeAccountNumberService.countFreeAccountNumbersByType(ACCOUNT_TYPE).intValue();
        freeAccountNumberScheduler.generateMissingAccounts();
        int newCount = freeAccountNumberService.countFreeAccountNumbersByType(ACCOUNT_TYPE).intValue();
        assertThat(newCount).isGreaterThanOrEqualTo(initialCount);
    }
}