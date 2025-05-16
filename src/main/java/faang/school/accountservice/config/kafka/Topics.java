package faang.school.accountservice.config.kafka;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class Topics {

    @Value("${spring.kafka.topics.authorization}")
    private String authorizationTopic;

    @Value("${spring.kafka.topics.cancellation}")
    private String cancellationTopic;

    @Value("${spring.kafka.topics.clearing}")
    private String clearingTopic;
}
