package faang.school.accountservice.repository;

import faang.school.accountservice.model.AccountNumber;
import faang.school.accountservice.model.AccountNumberId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@Testcontainers
@TestPropertySource(properties = "spring.test.database.replace=none")
public class FreeAccountNumberRepositoryTest {
    @Autowired
    private FreeAccountNumbersRepository freeAccountNumbersRepository;
    private AccountNumber accountNumber;
    private AccountNumberId accountNumberId;

    @Container
    public static final PostgreSQLContainer postgresContainer =
            new PostgreSQLContainer<>("postgres:13.3")
                    .withInitScript("init.sql");
    @DynamicPropertySource
    static void postgresqlProperties(DynamicPropertyRegistry registry) {
        postgresContainer.start();
        registry.add("spring.datasource.url", postgresContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgresContainer::getUsername);
        registry.add("spring.datasource.password", postgresContainer::getPassword);
    }

    @BeforeEach
    void setUp(){
        accountNumberId = new AccountNumberId(4200_0000_0000_0000L, "debit");
        accountNumber = new AccountNumber(accountNumberId);
        freeAccountNumbersRepository.save(accountNumber);

    }
}
