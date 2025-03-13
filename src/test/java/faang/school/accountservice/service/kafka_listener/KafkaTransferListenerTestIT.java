package faang.school.accountservice.service.kafka_listener;

import faang.school.accountservice.BaseIntegrationTest;
import faang.school.accountservice.config.kafka.KafkaTopicsProperties;
import faang.school.accountservice.dto.transfer_request.CancelMessageRequest;
import faang.school.accountservice.dto.transfer_request.ClearingMessageRequest;
import faang.school.accountservice.dto.transfer_request.TransferRequestDto;
import faang.school.accountservice.dto.transfer_request.TransferResponse;
import faang.school.accountservice.enums.Currency;
import faang.school.accountservice.enums.transfer_request.CancelType;
import faang.school.accountservice.enums.transfer_request.ClearingType;
import faang.school.accountservice.enums.transfer_request.TransferStatus;
import faang.school.accountservice.repository.TransferRepository;
import faang.school.accountservice.service.scheduled.TransferResponseSender;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.test.context.jdbc.Sql;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class KafkaTransferListenerIT extends BaseIntegrationTest {

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;
    @Autowired
    private KafkaTopicsProperties topicsProperties;
    @Autowired
    private TransferRepository transferRepository;
    @Autowired
    private TransferResponseSender transferResponseSender;

    private KafkaConsumer<String, Object> kafkaConsumer;
    private static final UUID FIRST_ID = UUID.fromString("5d14fdc1-8327-4cb0-b102-f59ff9cbf5d1");
    private static final UUID SECOND_ID = UUID.fromString("5d14fdc1-8327-4cb0-b102-f59ff9cbf5d2");

    @BeforeAll
    void beforeAll() {
        Properties props = getProperties();

        kafkaConsumer = new KafkaConsumer<>(props);
        kafkaConsumer.subscribe(List.of(
                topicsProperties.getTransferResponse(),
                topicsProperties.getTransferCancelResponse(),
                topicsProperties.getTransferClearingResponse()
        ));

        Awaitility.await().atMost(30, TimeUnit.SECONDS).until(() -> {
            kafkaConsumer.poll(Duration.ofMillis(100));
            return !kafkaConsumer.assignment().isEmpty();
        });
    }

    @Sql(scripts = "/cleanup-test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/test-data-accounts-balances.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Order(1)
    @Test
    void testListenNewAuthRequest() throws Exception {
        TransferRequestDto requestDto = createTransferRequestDto(FIRST_ID);
        TransferRequestDto secondRequestDto = createTransferRequestDto(SECOND_ID);
        kafkaTemplate.send(topicsProperties.getTransferRequest(), FIRST_ID.toString(), requestDto).get();
        kafkaTemplate.send(topicsProperties.getTransferRequest(), SECOND_ID.toString(), secondRequestDto).get();

        Awaitility.await()
                .atMost(30, TimeUnit.SECONDS)
                .pollInterval(Duration.ofMillis(500))
                .until(() -> checkExistsInDb(FIRST_ID, TransferStatus.AUTHORIZED)
                        && checkExistsInDb(SECOND_ID, TransferStatus.AUTHORIZED));

        transferResponseSender.retryUnsentPayments();

        Awaitility.await()
                .atMost(30, TimeUnit.SECONDS)
                .pollInterval(Duration.ofMillis(500))
                .until(() -> {
                    ConsumerRecords<String, Object> records = kafkaConsumer.poll(Duration.ofMillis(500));
                    for (ConsumerRecord<String, Object> record : records) {
                        if (checkIdAndStatus(record, FIRST_ID, topicsProperties.getTransferResponse(), TransferStatus.AUTHORIZED)) {
                            return true;
                        }
                        if (checkIdAndStatus(record, SECOND_ID, topicsProperties.getTransferResponse(), TransferStatus.AUTHORIZED)) {
                            return true;
                        }
                    }
                    return false;
                });
    }

    @Order(2)
    @Test
    void testListenCancelRequest() throws Exception {
        CancelMessageRequest cancelRequestFirst = new CancelMessageRequest(FIRST_ID, CancelType.TIMEOUT_CANCEL);
        kafkaTemplate.send(topicsProperties.getTransferCancelRequest(), FIRST_ID.toString(), cancelRequestFirst).get();

        Awaitility.await()
                .atMost(30, TimeUnit.SECONDS)
                .pollInterval(Duration.ofMillis(500))
                .until(() -> checkExistsInDb(FIRST_ID, TransferStatus.CANCELLED));

        transferResponseSender.retryUnsentPayments();

        CancelMessageRequest cancelRequestSecond = new CancelMessageRequest(FIRST_ID, CancelType.TIMEOUT_CANCEL);
        kafkaTemplate.send(topicsProperties.getTransferCancelRequest(), FIRST_ID.toString(), cancelRequestSecond).get();

        Awaitility.await().atMost(30, TimeUnit.SECONDS).until(() -> {
            ConsumerRecords<String, Object> records = kafkaConsumer.poll(Duration.ofMillis(500));
            for (ConsumerRecord<String, Object> record : records) {
                if (checkIdAndStatus(record, FIRST_ID, topicsProperties.getTransferCancelResponse(), TransferStatus.CANCELLED)) {
                    return true;
                }
                if (checkIdAndStatus(record, FIRST_ID, topicsProperties.getTransferCancelResponse(), TransferStatus.PAYMENT_ALREADY_CANCELLED)) {
                    return true;
                }
            }
            return false;
        });
    }

    @Order(3)
    @Test
    void testListenClearRequest() throws Exception {
        ClearingMessageRequest clearRequestFirst = new ClearingMessageRequest(SECOND_ID, ClearingType.CLEARING_BY_USER);
        kafkaTemplate.send(topicsProperties.getTransferClearingRequest(), SECOND_ID.toString(), clearRequestFirst).get();

        Awaitility.await()
                .atMost(30, TimeUnit.SECONDS)
                .pollInterval(Duration.ofMillis(500))
                .until(() -> checkExistsInDb(SECOND_ID, TransferStatus.CLEARED));

        transferResponseSender.retryUnsentPayments();

        ClearingMessageRequest clearRequestSecond = new ClearingMessageRequest(SECOND_ID, ClearingType.CLEARING_BY_USER);
        kafkaTemplate.send(topicsProperties.getTransferClearingRequest(), SECOND_ID.toString(), clearRequestSecond).get();

        Awaitility.await().atMost(30, TimeUnit.SECONDS).until(() -> {
            ConsumerRecords<String, Object> records = kafkaConsumer.poll(Duration.ofMillis(500));
            for (ConsumerRecord<String, Object> record : records) {
                if (checkIdAndStatus(record, SECOND_ID, topicsProperties.getTransferClearingResponse(), TransferStatus.CLEARED)) {
                    return true;
                }
                if (checkIdAndStatus(record, SECOND_ID, topicsProperties.getTransferClearingResponse(), TransferStatus.PAYMENT_ALREADY_CLEARED)) {
                    return true;
                }
            }
            return false;
        });
    }

    @Sql(scripts = "/cleanup-test-data.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Order(4)
    @Test
    void testValidationErrorHandled() throws Exception {
        TransferRequestDto invalidRequest = TransferRequestDto.builder()
                .id(UUID.randomUUID())
                .senderAccountNumber("987654321987")
                .receiverAccountNumber("123456789123")
                .amount(BigDecimal.valueOf(10))
                .currency(Currency.JEP)
                .build();

        kafkaTemplate.send(topicsProperties.getTransferRequest(), invalidRequest.id().toString(), invalidRequest).get();

        transferResponseSender.retryUnsentPayments();

        Awaitility.await().atMost(30, TimeUnit.SECONDS).until(() -> {
            ConsumerRecords<String, Object> records = kafkaConsumer.poll(Duration.ofMillis(500));
            for (ConsumerRecord<String, Object> record : records) {
                if (checkIdAndStatus(record, invalidRequest.id(), topicsProperties.getTransferResponse(), TransferStatus.ERROR)) {
                    return true;
                }
            }
            return false;
        });
    }

    private TransferRequestDto createTransferRequestDto(UUID id) {
        return TransferRequestDto.builder()
                .id(id)
                .senderAccountNumber("987654321987")
                .receiverAccountNumber("123456789123")
                .amount(BigDecimal.valueOf(10))
                .currency(Currency.USD)
                .build();
    }

    private Properties getProperties() {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, KAFKA_CONTAINER.getBootstrapServers());
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "test-group");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class.getName());
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, "10");
        props.put(ConsumerConfig.FETCH_MIN_BYTES_CONFIG, "1");
        props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "15000");
        props.put(ConsumerConfig.HEARTBEAT_INTERVAL_MS_CONFIG, "5000");
        props.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, "300000");
        return props;
    }

    private boolean checkIdAndStatus(ConsumerRecord<String, Object> record, UUID firstId, String topicsProperties, TransferStatus authorized) {
        if (record.key().equals(firstId.toString()) && record.topic().equals(topicsProperties)) {
            TransferResponse response = (TransferResponse) record.value();
            assertEquals(firstId, response.paymentId());
            assertEquals(authorized, response.result());
            kafkaConsumer.commitSync();
            return true;
        }
        return false;
    }

    private boolean checkExistsInDb(UUID id, TransferStatus status) {
        return transferRepository.findById(id).isPresent() &&
                transferRepository.findById(id).get().getStatus() == status;
    }
}