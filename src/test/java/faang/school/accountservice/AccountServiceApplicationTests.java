package faang.school.accountservice;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@Testcontainers
public class AccountServiceApplicationTests {

    @Container
    private static final PostgreSQLContainer<?> POSTGRESQL_CONTAINER =
            new PostgreSQLContainer<>("postgres:13.6");

    @Container
    private static final KafkaContainer KAFKA_CONTAINER = new KafkaContainer(
            DockerImageName.parse("confluentinc/cp-kafka:7.4.0")
    );

    @DynamicPropertySource
    static void containerProperties(DynamicPropertyRegistry registry) {
        POSTGRESQL_CONTAINER.start();
        KAFKA_CONTAINER.start();

        String postgresEndpoint = "jdbc:postgresql://" + POSTGRESQL_CONTAINER.getHost() + ":"
                + POSTGRESQL_CONTAINER.getMappedPort(5432) + "/postgres";
        registry.add("spring.datasource.url", () -> postgresEndpoint);
        registry.add("spring.datasource.username", POSTGRESQL_CONTAINER::getUsername);
        registry.add("spring.datasource.password", POSTGRESQL_CONTAINER::getPassword);

        registry.add("spring.kafka.bootstrap-servers", KAFKA_CONTAINER::getBootstrapServers);

        registry.add("spring.liquibase.change-log", () -> "classpath:db/changelog/db.changelog-test-master.yaml");

    }


    @Test
    void contextLoads() {
    }
}
