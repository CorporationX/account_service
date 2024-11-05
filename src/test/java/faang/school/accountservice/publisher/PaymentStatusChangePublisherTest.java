package faang.school.accountservice.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.accountservice.dto.PendingDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.Map;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentStatusChangePublisherTest {

    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private PaymentStatusChangePublisher paymentStatusChangePublisher;

    private String topicName;

    @BeforeEach
    void setUp() {
        topicName = "paymentStatusChangePublisherTest";
        ReflectionTestUtils.setField(paymentStatusChangePublisher, "topicName", topicName);
    }

    @Test
    void testPublishEvent() throws JsonProcessingException {
        String json = "json";
        PendingDto pendingDto = new PendingDto();
        pendingDto.setAmount(BigDecimal.valueOf(100.0));
        when(objectMapper.writeValueAsString(pendingDto)).thenReturn(json);

        paymentStatusChangePublisher.publish(pendingDto);

        verify(kafkaTemplate).send(topicName, json);
    }
}
