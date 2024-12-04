package faang.school.accountservice.config.kafka;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "spring.data.kafka")
public class KafkaProperties {

    private int capacity;
    private int corePoolSize;
    private int maxPoolSize;
    private long keepAliveTime;

    private Consumer consumer;
    private int interval;
    private int maxAttempts;
    private String isolationLevel;

    private String bootstrapServers;

    private Producer producer;
    private Map<String, String> properties;

    private String accountOpeningTopic;

    private int partitionsCount;
    private int replicaCount;
    private boolean idempotence;

    @Getter
    @Setter
    protected static class Producer {
        private String acks;
        private int retries;
        private String keySerializer;
        private String valueSerializer;
    }

    @Getter
    @Setter
    public static class Consumer {
        private String groupId;
        private String autoOffsetReset;
        private boolean enableAutoCommit;
    }
}
