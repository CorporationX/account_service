package faang.school.accountservice.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.utility.DockerImageName;

@TestConfiguration
public class TestContainersConfig {
    private static final String POSTGRES_IMAGE = "postgres:13.3";
    private static final String POSTGRES_DB_NAME = "postgres";
    private static final String POSTGRES_USERNAME = "user";
    private static final String POSTGRES_PASSWORD = "password";

    @Container
    public static PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>(
            DockerImageName.parse(POSTGRES_IMAGE))
            .withDatabaseName(POSTGRES_DB_NAME)
            .withUsername(POSTGRES_USERNAME)
            .withPassword(POSTGRES_PASSWORD);

    @DynamicPropertySource
    public static void dynamicProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgresContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgresContainer::getUsername);
        registry.add("spring.datasource.password", postgresContainer::getPassword);
    }
}
