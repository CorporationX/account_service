package faang.school.accountservice.config.kafka;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "spring.kafka.topic.request")
public class KafkaRequestTopicProperties {
    private String name;
    private int partitions;
}
