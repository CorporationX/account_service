package faang.school.accountservice.service.scheduled;

import faang.school.accountservice.BaseIntegrationTest;
import faang.school.accountservice.entity.TransferRequest;
import faang.school.accountservice.repository.TransferRepository;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TransferResponseSenderIT extends BaseIntegrationTest {
    private static final UUID TRANSFER_ID = UUID.fromString("5d14fdc1-8327-4cb0-b102-f59ff9cbf5d8");

    @Autowired
    private TransferResponseSender transferResponseSender;

    @Autowired
    private TransferRepository transferRepository;

    private TransferRequest transferRequest;

    @BeforeEach
    void setup() {
        Properties props = new Properties();
        props.put("bootstrap.servers", KAFKA_CONTAINER.getBootstrapServers());
        props.put("group.id", "test-group");
        props.put("key.deserializer", StringDeserializer.class.getName());
        props.put("value.deserializer", JsonDeserializer.class.getName());
        props.put("auto.offset.reset", "earliest");
        KafkaConsumer<String, Object> kafkaConsumer = new KafkaConsumer<>(props);
        kafkaConsumer.subscribe(List.of("transfer_response"));
    }

    @Order(1)
    @Sql(scripts = {
            "/cleanup-test-data.sql",
            "/test-data-accounts-balances.sql",
            "/test-data-auth-payments.sql",
            "/test-data-transfer-requests.sql",
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Test
    void testRetryUnsentPaymentsPositive() {
        transferRequest = transferRepository.findByIdOrThrow(TRANSFER_ID);
        assertThat(transferRequest.isKafkaPublished()).isFalse();

        transferResponseSender.retryUnsentPayments();

        TransferRequest updatedRequest = transferRepository.findById(TRANSFER_ID).orElseThrow();
        assertThat(updatedRequest.isKafkaPublished()).isTrue();
    }


    @Order(2)
    @Sql(scripts = "/cleanup-test-data.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void testScheduledRetryUnsentPayments() {
        transferRequest = transferRepository.findByIdOrThrow(TRANSFER_ID);

        transferRepository.save(transferRequest);

        await().atMost(30, TimeUnit.SECONDS).until(() -> {
            return transferRepository.findById(TRANSFER_ID).map(TransferRequest::isKafkaPublished).orElse(false);
        });

        TransferRequest updatedRequest = transferRepository.findById(TRANSFER_ID).orElseThrow();
        assertThat(updatedRequest.isKafkaPublished()).isTrue();
    }
}
