package faang.school.accountservice.kafka.producer;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
public class KafkaTopics {

    @Value("${spring.kafka.topics.request.name}")
    private String requestEventsTopic;
}
