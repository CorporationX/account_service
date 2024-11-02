package faang.school.accountservice.publisher.pending.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.accountservice.dto.balance.response.CheckingAccountBalance;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.UUID;

import static faang.school.accountservice.enums.pending.AccountBalanceStatus.SUFFICIENT_FUNDS;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PendingOperationToKafkaPublisherTest {
    private static final UUID OPERATION_ID = UUID.randomUUID();
    private static final UUID SOURCE_ACCOUNT_ID = UUID.randomUUID();
    private static final String TOPIC = "topic";
    private static final String JSON = "json";

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private PendingOperationToKafkaPublisher publisher;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(publisher, "topicName", TOPIC);
    }

    @Test
    void testPublish_jsonException() throws JsonProcessingException {
        CheckingAccountBalance checkingAccountBalance =
                new CheckingAccountBalance(OPERATION_ID, SOURCE_ACCOUNT_ID, SUFFICIENT_FUNDS);

        when(objectMapper.writeValueAsString(checkingAccountBalance)).thenThrow(new JsonProcessingException("Error") {
        });

        assertThatThrownBy(() -> publisher.publish(checkingAccountBalance)).isInstanceOf(RuntimeException.class);
    }

    @Test
    void testPublish_successful() throws JsonProcessingException {
        CheckingAccountBalance checkingAccountBalance =
                new CheckingAccountBalance(OPERATION_ID, SOURCE_ACCOUNT_ID, SUFFICIENT_FUNDS);

        when(objectMapper.writeValueAsString(checkingAccountBalance)).thenReturn(JSON);

        publisher.publish(checkingAccountBalance);

        verify(kafkaTemplate).send(TOPIC, JSON);
    }
}