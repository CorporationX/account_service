package faang.school.accountservice.config.kafka;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class KafkaProperties {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${spring.kafka.producer.retries}")
    private Integer producerRetries;

    @Value("${spring.kafka.producer.acks}")
    private String producerAcks;

    @Value("${spring.kafka.producer.linger-ms}")
    private Integer producerLingerMs;

    @Value("${spring.kafka.producer.batch-size}")
    private Integer producerBatchSize;

    @Value("${spring.kafka.consumer.group-id}")
    private String consumerGroupId;

    @Value("${spring.kafka.consumer.auto-offset-reset}")
    private String consumerAutoOffsetReset;

    @Value("${spring.kafka.consumer.enable-auto-commit}")
    private Boolean consumerEnableAutoCommit;

    @Value("${spring.kafka.consumer.max-poll-records}")
    private Integer consumerMaxPollRecords;
}