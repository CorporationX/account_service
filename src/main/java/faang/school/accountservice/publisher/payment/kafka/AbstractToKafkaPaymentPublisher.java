package faang.school.accountservice.publisher.payment.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.accountservice.aspect.publisher.payment.PaymentPublisher;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.kafka.core.KafkaTemplate;

@RequiredArgsConstructor
public abstract class AbstractToKafkaPaymentPublisher<T> implements PaymentPublisher<T> {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final Class<T> type;
    @Setter
    private T response;

    @Override
    public void publish() {
        try {
            String json = objectMapper.writeValueAsString(response);
            kafkaTemplate.send(getTopicName(), json);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Class<T> getInstance() {
        return type;
    }
}
