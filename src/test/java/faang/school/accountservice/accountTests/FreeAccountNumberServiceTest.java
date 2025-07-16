package faang.school.accountservice.accountTests;

import com.redis.testcontainers.RedisContainer;
import faang.school.accountservice.config.property.AccountTypeProperties;
import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.repository.AccountNumberSequenceRepository;
import faang.school.accountservice.repository.FreeAccountNumberRepository;
import faang.school.accountservice.service.FreeAccountNumberService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Testcontainers
public class FreeAccountNumberServiceTest {
    @Container
    public static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:13.6");
    @Container
    private static final RedisContainer REDIS_CONTAINER =
            new RedisContainer(DockerImageName.parse("redis/redis-stack:latest"));


    @DynamicPropertySource
    static void postgresqlProperties(DynamicPropertyRegistry registry) {
        postgres.start();
        REDIS_CONTAINER.start();

        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);

        registry.add("spring.data.redis.port", () -> REDIS_CONTAINER.getMappedPort(6379));
        registry.add("spring.data.redis.host", REDIS_CONTAINER::getHost);

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Autowired
    private AccountNumberSequenceRepository sequenceRepository;
    @Autowired
    private FreeAccountNumberRepository numberRepository;
    @Autowired
    private AccountTypeProperties accountTypeProperties;
    @Autowired
    private FreeAccountNumberService service;

    @Test
    void getNewNumberTest() {
        AccountType type = AccountType.BUDGET;
        String newNumber = service.getNewNumber(type);
        assertTrue(newNumber.startsWith(accountTypeProperties.getValue(type)));
        assertTrue(newNumber.length() >= 12);
        assertTrue(newNumber.length() <= 20);
    }

    @Test
    void setConsumerTest() {
        AccountType type = AccountType.BUDGET;
        Consumer<String> consumer = (number) -> System.out.println("Everything fine");
        service.setConsumer(type, consumer);
    }

    @Test
    void createNewCounterFailTest() {
        assertThrows(IllegalArgumentException.class, () -> service.createNewCounter(AccountType.BUDGET, "1313"));
    }
}
