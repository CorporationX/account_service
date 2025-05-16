package faang.school.accountservice.config.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class KafkaConfig {
    private final KafkaProperties properties;

    public Map<String, Object> getProducerConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, properties.getBootstrapServers());
        props.put(ProducerConfig.RETRIES_CONFIG, properties.getProducerRetries());
        props.put(ProducerConfig.ACKS_CONFIG, properties.getProducerAcks());
        props.put(ProducerConfig.LINGER_MS_CONFIG, properties.getProducerLingerMs());
        props.put(ProducerConfig.BATCH_SIZE_CONFIG, properties.getProducerBatchSize());
        return props;
    }

    public Map<String, Object> getConsumerConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, properties.getBootstrapServers());
        props.put(ConsumerConfig.GROUP_ID_CONFIG, properties.getConsumerGroupId());
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, properties.getConsumerAutoOffsetReset());
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, properties.getConsumerEnableAutoCommit());
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, properties.getConsumerMaxPollRecords());
        return props;
    }
}
