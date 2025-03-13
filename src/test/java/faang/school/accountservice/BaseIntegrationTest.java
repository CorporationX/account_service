package faang.school.accountservice;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.shaded.org.awaitility.Awaitility;
import org.testcontainers.utility.DockerImageName;

import java.util.concurrent.TimeUnit;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Testcontainers
public abstract class BaseIntegrationTest {
    public static final PostgreSQLContainer<?> POSTGRES_CONTAINER;
    public static final KafkaContainer KAFKA_CONTAINER;

    @LocalServerPort
    protected int port;

    protected WebTestClient webTestClient;

    @BeforeEach
    void setUpWebClient() {
        webTestClient = WebTestClient.bindToServer()
                .baseUrl("http://localhost:" + port)
                .defaultHeader("x-user-id", "1")
                .build();
    }

    static {
        POSTGRES_CONTAINER = new PostgreSQLContainer<>("postgres:13.3")
                .withDatabaseName("postgres")
                .withUsername("user")
                .withPassword("password");

        KAFKA_CONTAINER = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.3.1"));

        POSTGRES_CONTAINER.start();
        KAFKA_CONTAINER.start();

        Awaitility.await().atMost(30, TimeUnit.SECONDS).until(KAFKA_CONTAINER::isRunning);
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        String kafkaBootstrapServers = KAFKA_CONTAINER.getBootstrapServers();

        registry.add("spring.datasource.url", POSTGRES_CONTAINER::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES_CONTAINER::getUsername);
        registry.add("spring.datasource.password", POSTGRES_CONTAINER::getPassword);
        registry.add("spring.kafka.bootstrap-servers", () -> kafkaBootstrapServers);
        registry.add("spring.kafka.consumer.bootstrap-servers", KAFKA_CONTAINER::getBootstrapServers);
        registry.add("spring.kafka.producer.bootstrap-servers", KAFKA_CONTAINER::getBootstrapServers);
    }
}
