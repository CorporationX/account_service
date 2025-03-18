package faang.school.accountservice.config;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@Configuration
public class RateChangeKafkaProducerConfig {

    @Value("${spring.kafka.bootstrap.server.address}")
    private String bootstrapAddress;
    @Value("${spring.kafka.topics.notification-topics.rate-change-event-topic-name}")
    private String rateChangeEventTopicName;

    @Bean("rateChangeProducerFactory")
    public ProducerFactory<String, Object> rateChangeProducerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(
                ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
                bootstrapAddress);
        configProps.put(
                ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                StringSerializer.class.getName());
        configProps.put(
                ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                JsonSerializer.class.getName());
    //    configProps.put(JsonSerializer.TYPE_MAPPINGS, ("LikeEvent:faang.school.postservice.event.LikeEvent"));

        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean("rateChangeEventKafkaTemplate")
    public KafkaTemplate<String, Object> rateChangeEventKafkaTemplate() {
        return new KafkaTemplate<>(rateChangeProducerFactory());
    }

    @Bean
    public String rateChangeEventTopicName() {
        return rateChangeEventTopicName;
    }

    @Bean
    public KafkaAdmin  kafkaAdmin() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(
                ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
                bootstrapAddress);
        return new KafkaAdmin(configProps);
    }

    @Bean
    public NewTopic rateChangeEventTopic() {
        return new NewTopic("rate-change-event",3,(short) 1);
    }
}
