package faang.school.accountservice.config.kafka;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Setter
@Getter
@Configuration
public class KafkaProperties {
    @Value("${spring.kafka.bootstrap-servers}")
    private List<String> bootstrapServers;

    @Value("${spring.kafka.consumer.group-id}")
    private String groupId;

    @Value("${spring.kafka.consumer.retry-interval-ms}")
    private Long retryIntervalMillis;

    @Value("${spring.kafka.consumer.max-attempts}")
    private Long retryMaxAttempts;

    @Value("${spring.kafka.consumer.concurrent-threads-count}")
    private int concurrentThreadsCount;
}
