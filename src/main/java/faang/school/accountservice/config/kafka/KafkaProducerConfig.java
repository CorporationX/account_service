package faang.school.accountservice.config.kafka;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class KafkaProducerConfig {
    private final KafkaProperties kafkaProperties;
    private final JsonSerializer<Object> jsonSerializer;

    @Bean
    public Map<String, Object> producerConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
                kafkaProperties.getBootstrapServers());
        props.put(ProducerConfig.RETRIES_CONFIG,
                kafkaProperties.getProducerRetries());
        props.put(ProducerConfig.ACKS_CONFIG,
                kafkaProperties.getProducerAcks());
        props.put(ProducerConfig.LINGER_MS_CONFIG,
                kafkaProperties.getProducerLingerMs());
        props.put(ProducerConfig.BATCH_SIZE_CONFIG,
                kafkaProperties.getProducerBatchSize());
        return props;
    }

    // Универсальный Producer
    @Bean
    public ProducerFactory<String, Object> producerFactory() {
        return new DefaultKafkaProducerFactory<>(
                producerConfigs(),
                new StringSerializer(),
                jsonSerializer
        );
    }

    @Bean
    public KafkaTemplate<String, Object> dtoKafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }
}
